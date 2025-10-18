

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
