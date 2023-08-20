import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITriangulationReport, NewTriangulationReport } from '../triangulation-report.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITriangulationReport for edit and NewTriangulationReportFormGroupInput for create.
 */
type TriangulationReportFormGroupInput = ITriangulationReport | PartialWithRequiredKeyOf<NewTriangulationReport>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITriangulationReport | NewTriangulationReport> = Omit<T, 'date'> & {
  date?: string | null;
};

type TriangulationReportFormRawValue = FormValueOf<ITriangulationReport>;

type NewTriangulationReportFormRawValue = FormValueOf<NewTriangulationReport>;

type TriangulationReportFormDefaults = Pick<NewTriangulationReport, 'id' | 'date'>;

type TriangulationReportFormGroupContent = {
  id: FormControl<TriangulationReportFormRawValue['id'] | NewTriangulationReport['id']>;
  date: FormControl<TriangulationReportFormRawValue['date']>;
  name: FormControl<TriangulationReportFormRawValue['name']>;
  conclusion: FormControl<TriangulationReportFormRawValue['conclusion']>;
};

export type TriangulationReportFormGroup = FormGroup<TriangulationReportFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TriangulationReportFormService {
  createTriangulationReportFormGroup(triangulationReport: TriangulationReportFormGroupInput = { id: null }): TriangulationReportFormGroup {
    const triangulationReportRawValue = this.convertTriangulationReportToTriangulationReportRawValue({
      ...this.getFormDefaults(),
      ...triangulationReport,
    });
    return new FormGroup<TriangulationReportFormGroupContent>({
      id: new FormControl(
        { value: triangulationReportRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      date: new FormControl(triangulationReportRawValue.date),
      name: new FormControl(triangulationReportRawValue.name),
      conclusion: new FormControl(triangulationReportRawValue.conclusion),
    });
  }

  getTriangulationReport(form: TriangulationReportFormGroup): ITriangulationReport | NewTriangulationReport {
    return this.convertTriangulationReportRawValueToTriangulationReport(
      form.getRawValue() as TriangulationReportFormRawValue | NewTriangulationReportFormRawValue
    );
  }

  resetForm(form: TriangulationReportFormGroup, triangulationReport: TriangulationReportFormGroupInput): void {
    const triangulationReportRawValue = this.convertTriangulationReportToTriangulationReportRawValue({
      ...this.getFormDefaults(),
      ...triangulationReport,
    });
    form.reset(
      {
        ...triangulationReportRawValue,
        id: { value: triangulationReportRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TriangulationReportFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      date: currentTime,
    };
  }

  private convertTriangulationReportRawValueToTriangulationReport(
    rawTriangulationReport: TriangulationReportFormRawValue | NewTriangulationReportFormRawValue
  ): ITriangulationReport | NewTriangulationReport {
    return {
      ...rawTriangulationReport,
      date: dayjs(rawTriangulationReport.date, DATE_TIME_FORMAT),
    };
  }

  private convertTriangulationReportToTriangulationReportRawValue(
    triangulationReport: ITriangulationReport | (Partial<NewTriangulationReport> & TriangulationReportFormDefaults)
  ): TriangulationReportFormRawValue | PartialWithRequiredKeyOf<NewTriangulationReportFormRawValue> {
    return {
      ...triangulationReport,
      date: triangulationReport.date ? triangulationReport.date.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
