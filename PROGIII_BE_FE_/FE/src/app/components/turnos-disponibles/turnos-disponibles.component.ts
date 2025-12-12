import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AltaTurnoComponent } from '../alta-turno/alta-turno.component';
import { Turno, TurnoInput } from "../../models/turno.model";
import { Extractor } from "../../models/extractor.model";
import { TurnoService } from "../../services/turno.service";
import { ExtractorService } from "../../services/extractor.service";

@Component({
  selector: 'app-turnos-disponibles',
  standalone: true,
  imports: [CommonModule, FormsModule, AltaTurnoComponent],
  templateUrl: './turnos-disponibles.component.html',
})
export class TurnosDisponiblesComponent implements OnInit {
  turnosDisponibles: Turno[] = [];
  turnosFiltrados: Turno[] = [];
  extractores: Extractor[] = [];
  extractorSeleccionado: number | null = null;

  mostrarFormulario = false;
  turnoSeleccionado: Turno | null = null;

  constructor(
    private turnoService: TurnoService,
    private extractorService: ExtractorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarExtractores();
    this.cargarTurnosDisponibles();
  }

  cargarExtractores(): void {
    this.extractorService.getExtractores().subscribe({
      next: (extractores) => {
        this.extractores = extractores;
      },
      error: (error) => {
        console.error('Error al cargar extractores:', error);
      }
    });
  }

  cargarTurnosDisponibles(): void {
    this.turnoService.getTurnos({ estado: 'DISPONIBLE' }).subscribe({
      next: (turnos) => {
        this.turnosDisponibles = turnos;
        this.aplicarFiltro();
      },
      error: (error) => {
        console.error('Error al cargar turnos disponibles:', error);
      }
    });
  }

  aplicarFiltro(): void {
    if (this.extractorSeleccionado) {
      this.turnosFiltrados = this.turnosDisponibles.filter(
        turno => turno.extractor_id === this.extractorSeleccionado
      );
    } else {
      this.turnosFiltrados = [...this.turnosDisponibles];
    }
  }

  onFiltroChange(): void {
    this.aplicarFiltro();
  }

  seleccionarTurno(turno: Turno): void {
    this.turnoSeleccionado = turno;
    this.mostrarFormulario = true;
  }

  cancelarSeleccion(): void {
    this.mostrarFormulario = false;
    this.turnoSeleccionado = null;
  }

  onTurnoGuardado(): void {
    this.mostrarFormulario = false;
    this.turnoSeleccionado = null;
    this.router.navigate(['/turnos-ocupados']);
  }

  obtenerNombreExtractor(extractorId: number): string {
    const extractor = this.extractores.find(e => e.id === extractorId);
    return extractor ? extractor.nombre_completo : 'Sin asignar';
  }

  formatearFechaHora(fechaHora: string): string {
    const fecha = new Date(fechaHora);
    return fecha.toLocaleString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
