import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITriangulationPoint, NewTriangulationPoint } from '../triangulation-point.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITriangulationPoint for edit and NewTriangulationPointFormGroupInput for create.
 */
type TriangulationPointFormGroupInput = ITriangulationPoint | PartialWithRequiredKeyOf<NewTriangulationPoint>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITriangulationPoint | NewTriangulationPoint> = Omit<T, 'date'> & {
  date?: string | null;
};

type TriangulationPointFormRawValue = FormValueOf<ITriangulationPoint>;

type NewTriangulationPointFormRawValue = FormValueOf<NewTriangulationPoint>;

type TriangulationPointFormDefaults = Pick<NewTriangulationPoint, 'id' | 'date'>;

type TriangulationPointFormGroupContent = {
  id: FormControl<TriangulationPointFormRawValue['id'] | NewTriangulationPoint['id']>;
  frequency: FormControl<TriangulationPointFormRawValue['frequency']>;
  date: FormControl<TriangulationPointFormRawValue['date']>;
  description: FormControl<TriangulationPointFormRawValue['description']>;
  latitude: FormControl<TriangulationPointFormRawValue['latitude']>;
  longitude: FormControl<TriangulationPointFormRawValue['longitude']>;
  triangulationReport: FormControl<TriangulationPointFormRawValue['triangulationReport']>;
};

export type TriangulationPointFormGroup = FormGroup<TriangulationPointFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TriangulationPointFormService {
  createTriangulationPointFormGroup(triangulationPoint: TriangulationPointFormGroupInput = { id: null }): TriangulationPointFormGroup {
    const triangulationPointRawValue = this.convertTriangulationPointToTriangulationPointRawValue({
      ...this.getFormDefaults(),
      ...triangulationPoint,
    });
    return new FormGroup<TriangulationPointFormGroupContent>({
      id: new FormControl(
        { value: triangulationPointRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      frequency: new FormControl(triangulationPointRawValue.frequency, {
        validators: [Validators.required],
      }),
      date: new FormControl(triangulationPointRawValue.date),
      description: new FormControl(triangulationPointRawValue.description),
      latitude: new FormControl(triangulationPointRawValue.latitude, {
        validators: [Validators.required],
      }),
      longitude: new FormControl(triangulationPointRawValue.longitude, {
        validators: [Validators.required],
      }),
      triangulationReport: new FormControl(triangulationPointRawValue.triangulationReport),
    });
  }

  getTriangulationPoint(form: TriangulationPointFormGroup): ITriangulationPoint | NewTriangulationPoint {
    return this.convertTriangulationPointRawValueToTriangulationPoint(
      form.getRawValue() as TriangulationPointFormRawValue | NewTriangulationPointFormRawValue
    );
  }

  resetForm(form: TriangulationPointFormGroup, triangulationPoint: TriangulationPointFormGroupInput): void {
    const triangulationPointRawValue = this.convertTriangulationPointToTriangulationPointRawValue({
      ...this.getFormDefaults(),
      ...triangulationPoint,
    });
    form.reset(
      {
        ...triangulationPointRawValue,
        id: { value: triangulationPointRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TriangulationPointFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertTriangulationPointRawValueToTriangulationPoint(
    rawTriangulationPoint: TriangulationPointFormRawValue | NewTriangulationPointFormRawValue
  ): ITriangulationPoint | NewTriangulationPoint {
    return {
      ...rawTriangulationPoint,
      date: dayjs(rawTriangulationPoint.date, DATE_TIME_FORMAT),
    };
  }

  private convertTriangulationPointToTriangulationPointRawValue(
    triangulationPoint: ITriangulationPoint | (Partial<NewTriangulationPoint> & TriangulationPointFormDefaults)
  ): TriangulationPointFormRawValue | PartialWithRequiredKeyOf<NewTriangulationPointFormRawValue> {
    return {
      ...triangulationPoint,
      date: triangulationPoint.date ? triangulationPoint.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
