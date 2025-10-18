
# 🚀 BACKEND – Java Spring Boot

# 🧩 ENUNCIADO

## Sistema de Gestión de Turnos para Estudios de Laboratorio

Se debe desarrollar un **backend en Java con Spring Boot** para gestionar los _turnos de pacientes 
para estudios de laboratorio_. El sistema permitirá:

- Agendar un nuevo turno asociando un paciente con y extractor sobre un estudio en una fecha y horario determinado.
- Visualizar turnos agendados, con posibilidad de aplicar filtros.
- Programar turnos para fechas y horarios específicos, respetando reglas de negocio.

**Las demás funcionalidades del sistema ya están implementadas y se proporcionan como código base inicial.**

---

# 🚀 BACKEND – Java Spring Boot

## Requisitos Generales

- Spring Boot 3+.
- Arquitectura por capas: `Entity`, `DTO`, `Repository`, `Service`, `Controller`.
- Spring Data JPA.
- Base de datos: H2 en memoria.
- Validación con Jakarta Bean Validation (`@NotNull`, `@NotBlank`, etc.).
- Manejo correcto de errores con respuestas claras y códigos HTTP adecuados.
- ⚠️ Se debe **respetar estrictamente el contrato de la API** (rutas, formatos y validaciones).
- **Opcional:** Uso de `ModelMapper` para entidades y DTOs.

---

## 📄 Entidades

### Paciente

| Campo            | Tipo   | Obligatorio |
|------------------|--------|-------------|
| id               | Long   | No          |
| nombre_completo  | String | Sí          |
| fecha_nacimiento | String | No          |

### Estudio

| Campo          | Tipo   | Obligatorio |
|----------------|--------|-------------|
| id             | Long   | No          |
| nombre_estudio | String | Sí          |
| descripcion    | String | No          |

### Turno

| Campo          | Tipo          | Obligatorio |
|----------------|---------------|-------------|
| id             | Long          | No          |
| paciente       | Paciente      | Sí          |
| estudio        | Estudio       | Sí          |
| fechaHora      | LocalDateTime | Sí          |
| extractor      | Extractor     | Sí          |
| observaciones  | String        | No          |
| status         | String        | No          |

### Extractor

| Campo           | Tipo   | Obligatorio |
|-----------------|--------|-------------|
| id              | Long   | No          |
| nombre_completo | String | Sí          |
| matricula       | String | Sí          |

### ✅ Reglas de dominio

- 🔟 Máximo 48 turnos por día para todo el laboratorio. (existe un único Laboratorio físico)
- 🧍 Cada paciente puede tener solo 1 turno por día.
- ⏰ Turnos de 15 minutos entre las 07:00 y las 10:45 (inclusive).
- 🧪 Máximo 16 turnos por extractor por día (en intervalos de 15 minutos entre 07:00 y 10:45 (inclusive)).
- 👤 Cada extractor solo puede atender un paciente por franja de 15 minutos.
- ❌ No se permiten turnos fuera del horario laboral (07:00 a 10:45 (inclusive)).
- 📅 Fecha y hora deben tener formato yyyy-MM-dd'T'HH:mm:ss.
---

## 📦 data.sql

```sql
INSERT INTO estudios (id, nombre_estudio, descripcion)
VALUES (1, 'Análisis de Sangre', 'Requiere ayuno'),
       (2, 'Resonancia Magnética', 'No apto para personas con marcapasos'),
       (3, 'Ecografía Abdominal', 'Tomar agua previamente');

INSERT INTO pacientes (id, nombre_completo, fecha_nacimiento)
VALUES (1, 'Pedro González', '1990-01-01'),
       (2, 'María Fernández', '1985-02-02'),
       (3, 'Luis Martínez', '1970-03-03');

INSERT INTO extractores (id, nombre_completo, matricula)
VALUES (1, 'Laura Pérez', 'ENF1234'),
       (2, 'Carlos Méndez', 'ENF5678'),
       (3, 'Andrea López', 'ENF9012');
```

---

# 📡 ENDPOINTS A IMPLEMENTAR

---

### 📆 `GET /api/v1/turnos`

**Query params opcionales:**

- `estudio_id`
- `paciente_id`
- `extractor_id`
- `fecha_hora` (formato `yyyy-MM-dd'T'HH:mm:ss`)


> ❗❗**IMPORTANTE**❗❗: SOLO SE PUEDEN FILTRAR POR UN CAMPO A LA VEZ.

```json
[
  {
    "id": 100,
    "paciente": {
      "id": 1,
      "nombre_completo": "Pedro González"
    },
    "estudio": {
      "id": 1,
      "nombre_estudio": "Análisis de Sangre"
    },
    "extractor": {
      "id": 1,
      "nombre_completo": "Laura Pérez"
    },
    "fechaHora": "2025-06-20T08:00:00",
    "observaciones": "Ayuno completo"
  }
]
```

---

### ✅ `PUT /api/v1/turnos`

 - El paciente, extractor y estudio deben existir.
   - Si alguno no existe, se debe retornar un error `400` con el mensaje 
     `"Extractor, Paciente o Estudio no existen"`.
 - Unicamente se permite agendar un turno por paciente y día.
   - Si el paciente ya tiene un turno agendado para el día, se debe retornar un error `400` con el mensaje 
     `"El paciente ya tiene un turno agendado para el día indicado"`.
 - Un extractor no puede tomar más de un turno al mismo tiempo.
   - Si el extractor ya tiene un turno agendado para el día y hora indicados, se debe retornar un error `400` con el mensaje 
     `"Ya existe un turno para el extractor en esa fecha y hora"`.
 - El turno debe estar disponible y haber estado programado previamente.
   - Si el turno no está disponible o no ha sido programado, se debe retornar un error `400` con el mensaje 
     `"No hay turnos disponibles en ese horario para ese extractor"`.

```json
{
  "paciente_id": 1,
  "estudio_id": 2,
  "extractor_id": 2,
  "fechaHora": "2025-06-20T09:00:00",
  "observaciones": "Sin antecedentes"
}
```
---

### ➕ `POST /api/v1/turnos/programar?fecha=yyyy-MM-dd`

 - El parámetro `fecha` debe ser una fecha válida en formato `yyyy-MM-dd`.
 - Por cada extractor, se deben programar turnos cada 15 minutos entre las 07:00 y las 10:45 (inclusive).
 - Los turnos deben programarse con paciente, estudio y observaciones en `null` y status en `"DISPONIBLE"`.
 - Se debe validar que la fecha no esté programada ya verificando si existe algún turno para esa fecha.
   - Si la fecha ya tiene turnos programados, se debe retornar un error `400` con el mensaje 
     `"Los turnos ya están programados para la fecha indicada"`.

```
No body required
```

---

---

## ❗ MANEJO DE ERRORES

Todas las respuestas de error deben:

- Ser en **JSON**.
- Incluir código de estado HTTP.
- Contener un mensaje claro.

### Códigos esperados (Ejemplos)

| Código | Descripción                      |
|--------|----------------------------------|
| 400    | Validación fallida               |
| 404    | Paciente o estudio no encontrado |
| 500    | Error inesperado del servidor    |

---

## 🧪 TESTING

Se requiere:

- **Tests unitarios** para servicios (`@Service`)
- **Tests para controladores** (`@RestController`)

---

## ✅ CRITERIOS DE EVALUACIÓN – BACKEND

| Funcionalidad                                                                                                                                                                     | Puntos           |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------|
| `GET /api/v1/turnos`                                                                                                                                                              | 10               |
| `POST /api/v1/turnos`                                                                                                                                                             | 30               |
| `POST /api/v1/turnos/programar`                                                                                                                                                   | 30               |
| Tests Coverage > 80% en TurnoService y TurnoController <br/>Tests Coverage 50% - 80% en TurnoService y TurnoController<br/>Tests Coverage < 50% en TurnoService y TurnoController | 30<br/>20<br/>10 |

**Total: 100 puntos**

---


# 🖥 EXAMEN FRONTEND — Angular 19 + Tailwind CSS

## Sistema de Gestión de Turnos para Laboratorio

### Objetivo

Desarrollar los componentes *Turnos Disponibles* y *Alta de Turno* para el sistema de gestión de turnos de laboratorio, integrando con la API REST provista por el backend.  
La solución debe cumplir con los requisitos funcionales, de comunicación entre componentes y de estilos detallados a continuación.

---

## 📌 REQUISITOS GENERALES

- Utilizar *Angular 19* y *Tailwind CSS* para los estilos.
- El código debe estar organizado en componentes independientes y reutilizables.
- El consumo de la API debe realizarse mediante *servicios Angular* usando *Observables*.
- Todos los formularios deben ser *template-driven*.
- La comunicación entre componentes debe realizarse mediante *@Input* y *@Output* (no usar servicios globales para pasar datos entre componentes).
- El filtrado de turnos debe permitir seleccionar *solo un criterio a la vez* (paciente, estudio, extractor o fecha).
- Todo el estilado debe ser realizado con *Tailwind CSS*.

---

## 📝 FUNCIONALIDADES A IMPLEMENTAR

### 1. *Componente Turnos Disponibles*

- Debe mostrar una lista de turnos cuyo estado sea *DISPONIBLE*.
- Permitir filtrar los turnos por *extractor* mediante un combo/select.
- Al seleccionar un turno disponible, debe mostrarse el *formulario de alta de turno* (ver punto 2) con los datos del turno prellenados.
- Mientras se está completando el formulario, debe ocultarse la lista y el filtro de extractor, mostrando solo el formulario y un botón para cancelar la operación y volver a la lista.
- Al guardar el turno exitosamente, debe redirigir automáticamente al listado de turnos ocupados.
- El componente debe consumir la API /api/v1/turnos y /api/v1/extractores para obtener los datos necesarios.

### 2. *Componente Alta de Turno*

- Debe ser un formulario *template-driven* para agendar un turno.
- Los combos de *pacientes, **estudios* y *extractores* deben poblarse dinámicamente desde la API.
- El formulario debe recibir los datos del turno seleccionado mediante *@Input* y prellenar los campos correspondientes.
- Al guardar, debe enviar el turno por *POST* a /api/v1/turnos y manejar correctamente los mensajes de éxito o error.
- Al guardar exitosamente, debe emitir un evento (*@Output*) para que el componente padre pueda redirigir al listado de turnos ocupados.

### 3. *Componente Filtros y Grilla de Turnos*
- Debe implementar un método `filtrarTurnos()` que permita filtrar los turnos según el criterio seleccionado (paciente, estudio, extractor o fecha).
- Debe implementar un método `cargarTurnos(obj: FiltroTurnos)` que reciba un objeto con los filtros aplicados y cargue los turnos ocupados desde la API.
---

## 🌐 RUTAS REQUERIDAS

- /turnos-disponibles: Página donde se implementa el componente de turnos disponibles y el alta de turno.
- /turnos: Página de listado de turnos ocupados.
- /programar-turno: Página para crear los turnos disponibles.

---

## 📡 ENDPOINTS QUE DEBEN UTILIZARSE

- GET /api/v1/turnos  
  (con los parámetros necesarios para filtrar por extractor y obtener solo los turnos DISPONIBLES)
- GET /api/v1/extractores  
  (para poblar el combo de extractores)
- GET /api/v1/pacientes  
  (para poblar el combo de pacientes en el alta de turno)
- GET /api/v1/estudios  
  (para poblar el combo de estudios en el alta de turno)
- GET /api/v1/extractores/{id}/turnos 
  (para obtener los turnos de un extractor específico)
- PUT /api/v1/turnos  
  (para guardar/agendar un nuevo turno)

---

## ✅ CRITERIOS DE EVALUACIÓN

| Componente             | Total | Funcionalidad                              | Puntos |
|------------------------|-------|--------------------------------------------|--------|
| ⭐ *Turnos Disponibles* | *40*| Lista solo turnos DISPONIBLES              | 10     |
|                        |       | Filtro por extractor                       | 10     |
|                        |       | Selección y paso de datos al alta de turno | 10     |
|                        |       | Oculta lista/filtro al abrir el form       |  5     |
|                        |       | Redirección al listado tras guardar        |  5     |
| ⚙ *Alta de Turno*      | *40*| Formulario template-driven                 | 10     |
|                        |       | Combos dinámicos                           | 10     |
|                        |       | Prellenado por @Input                      | 10     |
|                        |       | POST y manejo de errores                   | 10     |
| 🔧 *Filtros y grilla*  | *20*| filtrarTurnos()                                 | 10     |
|                        |       | cargarTurnos(obj: FiltroTurnos)                        | 10     |

*Total: 100 puntos*

---

### 📌 Notas adicionales

- Se evaluará:
  - *Estructura de componentes:* cada uno debe ser independiente y reutilizable.
  - *Comunicación entre turnos disponibles y alta de turno* usando @Input/@Output, no usar servicios globales para pasar el turno seleccionado.
  - *Uso adecuado de forms template-driven y sus validaciones.*
  - *Consumo y manejo de errores de la API.*
  - *Estilo y UI empleando exclusivamente Tailwind.*
- Se penalizará:
  - Uso incorrecto de forms template-driven.
  - Uso de servicios globales o variables compartidas innecesarias para comunicación entre componentes.
  - Falta de manejo de errores.
  - No usar Tailwind CSS para los estilos.

---
