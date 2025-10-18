import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {TurnoService} from "../../services/turno.service";
import {Paciente} from "../../models/paciente.model";
import {Estudio} from "../../models/estudio.model";
import {Extractor} from "../../models/extractor.model";
import {Turno, TurnoInput} from "../../models/turno.model";
import {PacienteService} from "../../services/paciente.service";
import {EstudioService} from "../../services/estudio.service";
import {ExtractorService} from "../../services/extractor.service";
import {FormsModule, NgForm} from "@angular/forms";

@Component({
  selector: 'app-alta-turno',
  standalone: true,
  imports: [
    FormsModule
  ],
  templateUrl: './alta-turno.component.html'
})
export class AltaTurnoComponent implements OnInit, OnDestroy {
  private apiService = inject(TurnoService);
  private pacienteService = inject(PacienteService);
  private estudioService = inject(EstudioService);
  private extractorService = inject(ExtractorService);

  pacientes:Paciente[] =[];
  estudios:Estudio[] =[];
  extractores:Extractor[] =[];

  paciente: string = "";
  estudio: string = "";
  extractor: string = "";

  turno: TurnoInput = {
    paciente_id: 0,
    estudio_id: 0,
    extractor_id: 0,
    fechaHora:"",
    observaciones: "",

  }

  ngOnDestroy(): void {
  }

  ngOnInit(): void {
    this.pacienteService.getPacientes().subscribe(response => {
      this.pacientes = response;
    })

    this.estudioService.getEstudios().subscribe(response => {
      this.estudios = response;
    })

    this.extractorService.getExtractores().subscribe(response => {
      this.extractores = response;
    })
  }

  crearTurno(form: NgForm): void {
    if (form.valid){
      alert ("Faltan algunos campos por completar");
      return;
    }
    this.apiService.altaTurno(this.turno).subscribe(response => {
      alert("Reserva creada con éxito")
    })
  }

}
