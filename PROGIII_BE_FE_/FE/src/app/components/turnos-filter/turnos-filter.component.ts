import { Component, EventEmitter, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ExtractorService } from '../../services/extractor.service';
import { PacienteService } from '../../services/paciente.service';
import { EstudioService } from '../../services/estudio.service';
import { FiltroTurnos } from '../../models/turno.model';
import { Paciente } from '../../models/paciente.model';
import { Estudio } from '../../models/estudio.model';
import { Extractor } from '../../models/extractor.model';

@Component({
  selector: 'app-turnos-filter',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './turnos-filter.component.html'
})
export class TurnosFilterComponent {

  @Output() aplicarFiltro = new EventEmitter<FiltroTurnos>();

  pacientes: Paciente[] = [];
  estudios: Estudio[] = [];
  extractores: Extractor[] = [];

  filtro: FiltroTurnos = {};
  tipoFiltro: string = '';

  constructor(
    private pacienteSrv: PacienteService,
    private estudioSrv: EstudioService,
    private extractorSrv: ExtractorService
  ) {}

  ngOnInit(): void {
    this.pacienteSrv.getPacientes().subscribe(resp => this.pacientes = resp);
    this.estudioSrv.getEstudios().subscribe(resp => this.estudios = resp);
    this.extractorSrv.getExtractores().subscribe(resp => this.extractores = resp);
  }

  limpiarFiltros(excepto: string) {
    if (excepto !== 'paciente_id') this.filtro.paciente_id = undefined;
    if (excepto !== 'estudio_id') this.filtro.estudio_id = undefined;
    if (excepto !== 'extractor_id') this.filtro.extractor_id = undefined;
    if (excepto !== 'fecha') this.filtro.fecha = undefined;
  }

  onTipoFiltroChange() {
    this.filtro = {};
  }

  filtrarTurnos() {
    const filtro: FiltroTurnos = {};

    if (this.tipoFiltro === 'paciente' && this.filtro.paciente_id) {
      filtro.paciente_id = this.filtro.paciente_id;
    }

    if (this.tipoFiltro === 'estudio' && this.filtro.estudio_id) {
      filtro.estudio_id = this.filtro.estudio_id;
    }

    if (this.tipoFiltro === 'extractor' && this.filtro.extractor_id) {
      filtro.extractor_id = this.filtro.extractor_id;
    }

    if (this.tipoFiltro === 'fecha' && this.filtro.fecha) {
      filtro.fecha = this.filtro.fecha;
    }

    this.aplicarFiltro.emit(filtro);
  }
}

