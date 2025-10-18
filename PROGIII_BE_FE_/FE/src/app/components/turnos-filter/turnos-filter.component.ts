import {Component, EventEmitter, inject, Output} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgForOf } from '@angular/common';
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

  private apiService = inject(EstudioService);

  pacientes: Paciente[] = [];
  estudios: Estudio[] = [];
  extractores: Extractor[] = [];
  filtro: FiltroTurnos = {};
  tipoFiltro: string = '';

  constructor(
    private pacienteSrv: PacienteService,
    private estudioSrv: EstudioService,
    private extractorSrv: ExtractorService
  ) {
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
  ngOnInit(): void {
    this.apiService.getEstudios().subscribe(response => {
      this.estudios = response;
    })}

  filtrarTurnos() {
      // TODO: Implementar la lógica para construir el objeto `FiltroTurnos` basado en el tipo de filtro seleccionado.
      // TODO: Validar que los valores de los filtros sean correctos antes de enviarlos.
      // TODO: Emitir el evento `aplicarFiltro` con el objeto construido.
      // TODO: Asegurarse de que el formato de la fecha sea correcto si se selecciona el filtro por fecha.
    this.aplicarFiltro.emit(this.filtro);
  }

  onTipoFiltroChange() {
    // Limpiar todos los filtros menos el seleccionado
    if (this.tipoFiltro !== 'paciente') this.filtro.paciente_id = undefined;
    if (this.tipoFiltro !== 'estudio') this.filtro.estudio_id = undefined;
    if (this.tipoFiltro !== 'extractor') this.filtro.extractor_id = undefined;
    if (this.tipoFiltro !== 'fecha') this.filtro.fecha = undefined;
  }
}
