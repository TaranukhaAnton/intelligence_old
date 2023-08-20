import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITriangulationReport, NewTriangulationReport } from '../triangulation-report.model';

export type PartialUpdateTriangulationReport = Partial<ITriangulationReport> & Pick<ITriangulationReport, 'id'>;

type RestOf<T extends ITriangulationReport | NewTriangulationReport> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestTriangulationReport = RestOf<ITriangulationReport>;

export type NewRestTriangulationReport = RestOf<NewTriangulationReport>;

export type PartialUpdateRestTriangulationReport = RestOf<PartialUpdateTriangulationReport>;

export type EntityResponseType = HttpResponse<ITriangulationReport>;
export type EntityArrayResponseType = HttpResponse<ITriangulationReport[]>;

@Injectable({ providedIn: 'root' })
export class TriangulationReportService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/triangulation-reports');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(triangulationReport: NewTriangulationReport): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(triangulationReport);
    return this.http
      .post<RestTriangulationReport>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(triangulationReport: ITriangulationReport): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(triangulationReport);
    return this.http
      .put<RestTriangulationReport>(`${this.resourceUrl}/${this.getTriangulationReportIdentifier(triangulationReport)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(triangulationReport: PartialUpdateTriangulationReport): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(triangulationReport);
    return this.http
      .patch<RestTriangulationReport>(`${this.resourceUrl}/${this.getTriangulationReportIdentifier(triangulationReport)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTriangulationReport>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTriangulationReport[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTriangulationReportIdentifier(triangulationReport: Pick<ITriangulationReport, 'id'>): number {
    return triangulationReport.id;
  }

  compareTriangulationReport(o1: Pick<ITriangulationReport, 'id'> | null, o2: Pick<ITriangulationReport, 'id'> | null): boolean {
    return o1 && o2 ? this.getTriangulationReportIdentifier(o1) === this.getTriangulationReportIdentifier(o2) : o1 === o2;
  }

  addTriangulationReportToCollectionIfMissing<Type extends Pick<ITriangulationReport, 'id'>>(
    triangulationReportCollection: Type[],
    ...triangulationReportsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const triangulationReports: Type[] = triangulationReportsToCheck.filter(isPresent);
    if (triangulationReports.length > 0) {
      const triangulationReportCollectionIdentifiers = triangulationReportCollection.map(
        triangulationReportItem => this.getTriangulationReportIdentifier(triangulationReportItem)!
      );
      const triangulationReportsToAdd = triangulationReports.filter(triangulationReportItem => {
        const triangulationReportIdentifier = this.getTriangulationReportIdentifier(triangulationReportItem);
        if (triangulationReportCollectionIdentifiers.includes(triangulationReportIdentifier)) {
          return false;
        }
        triangulationReportCollectionIdentifiers.push(triangulationReportIdentifier);
        return true;
      });
      return [...triangulationReportsToAdd, ...triangulationReportCollection];
    }
    return triangulationReportCollection;
  }

  protected convertDateFromClient<T extends ITriangulationReport | NewTriangulationReport | PartialUpdateTriangulationReport>(
    triangulationReport: T
  ): RestOf<T> {
    return {
      ...triangulationReport,
      date: triangulationReport.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTriangulationReport: RestTriangulationReport): ITriangulationReport {
    return {
      ...restTriangulationReport,
      date: restTriangulationReport.date ? dayjs(restTriangulationReport.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTriangulationReport>): HttpResponse<ITriangulationReport> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTriangulationReport[]>): HttpResponse<ITriangulationReport[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
