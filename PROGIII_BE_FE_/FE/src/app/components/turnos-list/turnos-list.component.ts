import {Component, Input, OnInit} from '@angular/core';
import { TurnoService } from '../../services/turno.service';
import { CommonModule, DatePipe } from '@angular/common';
import { TurnosFilterComponent } from '../turnos-filter/turnos-filter.component';
import { FiltroTurnos, Turno } from '../../models/turno.model';

@Component({
  selector: 'app-turnos-list',
  standalone: true,
  imports: [
    DatePipe,
    TurnosFilterComponent,
    CommonModule
  ],
  templateUrl: './turnos-list.component.html'
})
export class TurnosListComponent implements OnInit {
  @Input() turnos: Turno[] = [];
  filtro: FiltroTurnos = {};
  error: string = '';

  constructor(private tSrv: TurnoService) {}

  ngOnInit() {
    this.cargarTurnos({});
  }

  onFiltrar(obj: FiltroTurnos) {
    this.filtro = obj;
    this.cargarTurnos(obj);
  }

  cargarTurnos(obj: FiltroTurnos) {
    this.error = "";
    this.turnos = [];

    const filtroConEstado: FiltroTurnos = {
      ...obj,
      estado: 'OCUPADO'
    };

    this.tSrv.getTurnos(filtroConEstado).subscribe({
      next: (resp) => {
        this.turnos = resp.filter(t => t.estado === 'OCUPADO');
      },
      error: () => {
        this.error = "Hubo un problema al cargar los turnos. Intente nuevamente.";
      }
    });
  }
}
