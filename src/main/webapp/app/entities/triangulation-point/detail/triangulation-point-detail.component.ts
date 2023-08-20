import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITriangulationPoint } from '../triangulation-point.model';

@Component({
  selector: 'jhi-triangulation-point-detail',
  templateUrl: './triangulation-point-detail.component.html',
})
export class TriangulationPointDetailComponent implements OnInit {
  triangulationPoint: ITriangulationPoint | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ triangulationPoint }) => {
      this.triangulationPoint = triangulationPoint;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
