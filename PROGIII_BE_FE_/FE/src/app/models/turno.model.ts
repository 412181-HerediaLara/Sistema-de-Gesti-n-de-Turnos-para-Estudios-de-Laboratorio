import { Paciente } from './paciente.model';
import { Estudio } from './estudio.model';
import { Extractor } from './extractor.model';

export interface Turno {
  id: number;
  paciente_id: number;
  estudio_id: number;
  extractor_id: number;
  fechaHora: string;
  estado: 'DISPONIBLE' | 'OCUPADO' | 'CANCELADO';
  observaciones?: string;
  created_at?: string;
  updated_at?: string;

  paciente?: Paciente;
  estudio?: Estudio;
  extractor?: Extractor;
}

export interface TurnoInput {
  paciente_id: number;
  estudio_id: number;
  extractor_id: number;
  fechaHora: string;
  observaciones?: string;
}

export interface FiltroTurnos {
  paciente_id?: number;
  estudio_id?: number;
  extractor_id?: number;
  fecha?: string;
  estado?: 'DISPONIBLE' | 'OCUPADO' | 'CANCELADO';
}
