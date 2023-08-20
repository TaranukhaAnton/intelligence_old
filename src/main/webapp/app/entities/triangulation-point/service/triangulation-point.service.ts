import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITriangulationPoint, NewTriangulationPoint } from '../triangulation-point.model';

export type PartialUpdateTriangulationPoint = Partial<ITriangulationPoint> & Pick<ITriangulationPoint, 'id'>;

type RestOf<T extends ITriangulationPoint | NewTriangulationPoint> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestTriangulationPoint = RestOf<ITriangulationPoint>;

export type NewRestTriangulationPoint = RestOf<NewTriangulationPoint>;

export type PartialUpdateRestTriangulationPoint = RestOf<PartialUpdateTriangulationPoint>;

export type EntityResponseType = HttpResponse<ITriangulationPoint>;
export type EntityArrayResponseType = HttpResponse<ITriangulationPoint[]>;

@Injectable({ providedIn: 'root' })
export class TriangulationPointService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/triangulation-points');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(triangulationPoint: NewTriangulationPoint): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(triangulationPoint);
    return this.http
      .post<RestTriangulationPoint>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(triangulationPoint: ITriangulationPoint): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(triangulationPoint);
    return this.http
      .put<RestTriangulationPoint>(`${this.resourceUrl}/${this.getTriangulationPointIdentifier(triangulationPoint)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(triangulationPoint: PartialUpdateTriangulationPoint): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(triangulationPoint);
    return this.http
      .patch<RestTriangulationPoint>(`${this.resourceUrl}/${this.getTriangulationPointIdentifier(triangulationPoint)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTriangulationPoint>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTriangulationPoint[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTriangulationPointIdentifier(triangulationPoint: Pick<ITriangulationPoint, 'id'>): number {
    return triangulationPoint.id;
  }

  compareTriangulationPoint(o1: Pick<ITriangulationPoint, 'id'> | null, o2: Pick<ITriangulationPoint, 'id'> | null): boolean {
    return o1 && o2 ? this.getTriangulationPointIdentifier(o1) === this.getTriangulationPointIdentifier(o2) : o1 === o2;
  }

  addTriangulationPointToCollectionIfMissing<Type extends Pick<ITriangulationPoint, 'id'>>(
    triangulationPointCollection: Type[],
    ...triangulationPointsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const triangulationPoints: Type[] = triangulationPointsToCheck.filter(isPresent);
    if (triangulationPoints.length > 0) {
      const triangulationPointCollectionIdentifiers = triangulationPointCollection.map(
        triangulationPointItem => this.getTriangulationPointIdentifier(triangulationPointItem)!
      );
      const triangulationPointsToAdd = triangulationPoints.filter(triangulationPointItem => {
        const triangulationPointIdentifier = this.getTriangulationPointIdentifier(triangulationPointItem);
        if (triangulationPointCollectionIdentifiers.includes(triangulationPointIdentifier)) {
          return false;
        }
        triangulationPointCollectionIdentifiers.push(triangulationPointIdentifier);
        return true;
      });
      return [...triangulationPointsToAdd, ...triangulationPointCollection];
    }
    return triangulationPointCollection;
  }

  protected convertDateFromClient<T extends ITriangulationPoint | NewTriangulationPoint | PartialUpdateTriangulationPoint>(
    triangulationPoint: T
  ): RestOf<T> {
    return {
      ...triangulationPoint,
      date: triangulationPoint.date?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTriangulationPoint: RestTriangulationPoint): ITriangulationPoint {
    return {
      ...restTriangulationPoint,
      date: restTriangulationPoint.date ? dayjs(restTriangulationPoint.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTriangulationPoint>): HttpResponse<ITriangulationPoint> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTriangulationPoint[]>): HttpResponse<ITriangulationPoint[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
