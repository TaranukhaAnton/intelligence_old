import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITriangulationPoint } from '../triangulation-point.model';
import { TriangulationPointService } from '../service/triangulation-point.service';

@Injectable({ providedIn: 'root' })
export class TriangulationPointRoutingResolveService implements Resolve<ITriangulationPoint | null> {
  constructor(protected service: TriangulationPointService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITriangulationPoint | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((triangulationPoint: HttpResponse<ITriangulationPoint>) => {
          if (triangulationPoint.body) {
            return of(triangulationPoint.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
