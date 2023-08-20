import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITriangulationReport } from '../triangulation-report.model';
import { TriangulationReportService } from '../service/triangulation-report.service';

@Injectable({ providedIn: 'root' })
export class TriangulationReportRoutingResolveService implements Resolve<ITriangulationReport | null> {
  constructor(protected service: TriangulationReportService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITriangulationReport | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((triangulationReport: HttpResponse<ITriangulationReport>) => {
          if (triangulationReport.body) {
            return of(triangulationReport.body);
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
