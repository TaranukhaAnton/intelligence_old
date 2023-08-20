package ua.intelligence.service.impl;

import org.docx4j.TextUtils;
import org.docx4j.TraversalUtil;
import org.docx4j.finders.TableFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;
import ua.intelligence.domain.Message;
import ua.intelligence.service.DocxService;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DocxServiceImpl implements DocxService {
    public static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public List<Message> extractMessagesFromDocx(byte[] content) {
        List<Message> messages = new LinkedList<Message>();
        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new ByteArrayInputStream(content));
            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
            TableFinder finder = new TableFinder();
            new TraversalUtil(mainDocumentPart.getContent(), finder);

            final int size = finder.tblList.size();
            if (size != 3) {
                System.out.println("Error/ count of tables MUST be 3");
                return messages;
            }

            final List<Tbl> tblList = finder.tblList;
            final Tbl tbl = tblList.get(1);
            for (Object o : tbl.getContent()) {
                final List<Object> rowContent = ((Tr) o).getContent();
                if (rowContent.size() == 7) {
                    final String text = getString(rowContent, 5);
                    if (!text.equals("Зміст радіообміну") && !text.isEmpty()) {
                        Message message = new Message();
                        message.setFrequency(getString(rowContent, 0));
                        message.setDate(getZonedDateTime(getString(rowContent, 1), getString(rowContent, 2)));
                        message.setSenderCallSign(getString(rowContent, 3));
                        message.setReceiverCallSign(getString(rowContent, 4));
                        message.setText(text);
                        messages.add(message);
                    }
                }
            }
        } catch (Docx4JException docx4JException) {
            docx4JException.printStackTrace();
        }
        return messages;


    }

    @Override
    public byte[] processReport(byte[] content, Set<Message> messages, String conclusion) {



        List<List<String>> messagesSplitted = new LinkedList<>();
        for (Message message : messages) {
            messagesSplitted.add(List.of(message.getText().split("\n")));
        }




        try {

            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new ByteArrayInputStream(content));


            MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();


            mainDocumentPart.getContent().add(getTable(wordPackage, messagesSplitted));



            ObjectFactory factory = Context.getWmlObjectFactory();
            org.docx4j.wml.P  para = factory.createP();
            final String[] split = conclusion.split("\n");
            for (String s : split) {
                org.docx4j.wml.Text  t = factory.createText();
                t.setValue(s);
                org.docx4j.wml.R  run = factory.createR();
                run.getContent().add(t);
                para.getContent().add(run);
                Br br = factory.createBr();
                para.getContent().add(br);
            }
            mainDocumentPart.getContent().add( para);




            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            wordPackage.save(outStream);
            return  outStream.toByteArray();


        } catch (Docx4JException e) {
            throw new RuntimeException(e);
        }
    }

    private String getString(List<Object> rowContent, int index) {
        final List<Object> c = ((Tc) ((JAXBElement) rowContent.get(index)).getValue()).getContent();

        return c.stream().map(temp -> {
            Writer w = new CharArrayWriter();
            try {
                TextUtils.extractText(temp, w);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return w.toString();

        }).collect(Collectors.joining("\n"));
    }


    private static ZonedDateTime getZonedDateTime(String d, String t) {
        LocalDate localDate2 = LocalDate.parse(d, DATE_FORMATTER);
        LocalTime localTime2 = LocalTime.parse(t, DateTimeFormatter.ISO_LOCAL_TIME);
        ZoneId zoneId2 = ZoneId.of("GMT+03:00");
        return ZonedDateTime.of(localDate2, localTime2, zoneId2);
    }

    private static Tbl getTable(WordprocessingMLPackage wPMLpackage, List<List<String>> messages) {

        ObjectFactory factory = Context.getWmlObjectFactory();
        int writableWidthTwips = wPMLpackage.getDocumentModel().getSections()
            .get(0).getPageDimensions()
            .getWritableWidthTwips();

        int cols = 2;
        int cellWidthTwips = Double.valueOf(Math.floor((writableWidthTwips / cols))).intValue();

        final int size = messages.size();
        int tSize = size / 2;
        if (size % 2 > 0) tSize++;

        Tbl table = TblFactory.createTable(tSize, cols, cellWidthTwips);


        int ind = 0;
        for (int i = 0; i < tSize; i++) {
            Tr row = (Tr) table.getContent().get(i);
            for (int d = 0; d < 2; d++) {
                Tc column = (Tc) row.getContent().get(d);
                P columnPara = (P) column.getContent().get(0);


                if (ind < messages.size()) {
                    final List<String> strings = messages.get(ind);
                    for (String string : strings) {
                        Text tx = factory.createText();
                        R run = factory.createR();
                        tx.setValue(string);
                        run.getContent().add(tx);
                        columnPara.getContent().add(run);
                        Br br = factory.createBr();
                        columnPara.getContent().add(br);
                    }


                } else {
                    Text tx = factory.createText();
                    R run = factory.createR();
                    tx.setValue("");
                    run.getContent().add(tx);
                    columnPara.getContent().add(run);
                }
                ind++;

            }
        }
        addBorders(table);
        return table;
    }

    public static void addBorders(Tbl table) {
        table.setTblPr(new TblPr());
        CTBorder border = new CTBorder();
        border.setColor("auto");
        border.setSz(new BigInteger("4"));
        border.setSpace(new BigInteger("0"));
        border.setVal(STBorder.SINGLE);

        TblBorders borders = new TblBorders();
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);
        borders.setTop(border);
        borders.setInsideH(border);
        borders.setInsideV(border);
        table.getTblPr().setTblBorders(borders);
    }

}
