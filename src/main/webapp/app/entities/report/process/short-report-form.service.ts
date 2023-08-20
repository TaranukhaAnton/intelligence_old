import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReport } from '../report.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReport for edit
 */
type ReportFormGroupInput = IReport ;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReport > = Omit<T, 'date'> & {
  date?: string ;
};

type ReportFormRawValue = FormValueOf<IReport>;





type ReportFormGroupContent = {
  id: FormControl<ReportFormRawValue['id'] >;
  conclusion: FormControl<ReportFormRawValue['conclusion']>;
};

export type ReportFormGroup = FormGroup<ReportFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ShortReportFormService {
  createReportFormGroup(report: ReportFormGroupInput ): ReportFormGroup {
    const reportRawValue = this.convertReportToReportRawValue({

      ...report,
    });
    return new FormGroup<ReportFormGroupContent>({
      id: new FormControl(
        { value: reportRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),

      conclusion: new FormControl(reportRawValue.conclusion),

    });
  }

  getReport(form: ReportFormGroup): IReport  {
    return this.convertReportRawValueToReport(form.getRawValue() as ReportFormRawValue );
  }





  private convertReportRawValueToReport(rawReport: ReportFormRawValue ): IReport  {
    return {
      ...rawReport,
      date: dayjs(rawReport.date, DATE_TIME_FORMAT),
    };
  }

  private convertReportToReportRawValue(
    report: IReport
  ): ReportFormRawValue  {
    return {
      ...report,
      date: report.date ? report.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }

  resetForm(form: ReportFormGroup, report: ReportFormGroupInput): void {
    const reportRawValue = this.convertReportToReportRawValue({ ...report });
    form.reset(
      {
        ...reportRawValue,
        id: { value: reportRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }
}
