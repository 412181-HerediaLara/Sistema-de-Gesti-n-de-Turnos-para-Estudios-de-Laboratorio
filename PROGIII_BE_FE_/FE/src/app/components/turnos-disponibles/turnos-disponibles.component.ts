import { Component } from '@angular/core';
import { AltaTurnoComponent } from '../alta-turno/alta-turno.component';
import {Estudio} from "../../models/estudio.model";
import {EstudioService} from "../../services/estudio.service";

@Component({
    selector: 'app-turnos-disponibles',
    standalone: true,
    imports: [AltaTurnoComponent],
    templateUrl: './turnos-disponibles.component.html',
})
export class TurnosDisponiblesComponent {
  estudios: Estudio[] = [];

  constructor(private estudioSrv: EstudioService) {
    this.estudioSrv.getEstudios().subscribe(resp => this.estudios = resp);
  }
}
