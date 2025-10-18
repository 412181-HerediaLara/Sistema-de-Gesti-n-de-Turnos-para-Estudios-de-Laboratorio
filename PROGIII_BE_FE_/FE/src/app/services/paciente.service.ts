import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Paciente } from '../models/paciente.model';
import {ApiConfig} from "./config-api";

@Injectable({ providedIn: 'root' })
export class PacienteService {
  constructor(private http: HttpClient) { }

  getPacientes(): Observable<Paciente[]> {
    return this.http.get<Paciente[]>(ApiConfig.PACIENTES);
  }
}
