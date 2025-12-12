import {Component, EventEmitter, Input, OnInit, Output, inject} from '@angular/core';
import {TurnoService} from "../../services/turno.service";
import {PacienteService} from "../../services/paciente.service";
import {EstudioService} from "../../services/estudio.service";
import {ExtractorService} from "../../services/extractor.service";
import {Paciente} from "../../models/paciente.model";
import {Estudio} from "../../models/estudio.model";
import {Extractor} from "../../models/extractor.model";
import {Turno, TurnoInput} from "../../models/turno.model";
import {FormsModule, NgForm} from "@angular/forms";

@Component({
  selector: 'app-alta-turno',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './alta-turno.component.html'
})
export class AltaTurnoComponent implements OnInit {

  @Input() turnoSeleccionado : Turno | null = null;
  @Output() turnoGuardado = new EventEmitter<void>();

  private apiService = inject(TurnoService);
  private pacienteService = inject(PacienteService);
  private estudioService = inject(EstudioService);
  private extractorService = inject(ExtractorService);

  pacientes: Paciente[] = [];
  estudios: Estudio[] = [];
  extractores: Extractor[] = [];

  turno: TurnoInput = {
    paciente_id: 0,
    estudio_id: 0,
    extractor_id: 0,
    fechaHora: "",
    observaciones: ""
  };

  ngOnInit(): void {
    this.pacienteService.getPacientes().subscribe(resp => this.pacientes = resp);
    this.estudioService.getEstudios().subscribe(resp => this.estudios = resp);
    this.extractorService.getExtractores().subscribe(resp => this.extractores = resp);

    if (this.turnoSeleccionado) {
      this.turno = {
        paciente_id: this.turnoSeleccionado.paciente_id,
        estudio_id: this.turnoSeleccionado.estudio_id,
        extractor_id: this.turnoSeleccionado.extractor_id,
        fechaHora: this.turnoSeleccionado.fechaHora,
        observaciones: this.turnoSeleccionado.observaciones
      };
    }
  }

  crearTurno(form: NgForm): void {
    if (!form.valid) {
      alert("Faltan algunos campos por completar");
      return;
    }

    this.apiService.altaTurno(this.turno).subscribe({
      next: () => {
        alert("Reserva creada con éxito");
        this.turnoGuardado.emit();
      },
      error: () => {
        alert("Ocurrió un error al guardar el turno");
      }
    });
  }
}
