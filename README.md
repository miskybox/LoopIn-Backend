🚀 Loopin - Backend
Loopin es la plataforma desarrollada por el equipo Code Crafters para conectar a la comunidad tecnológica global mediante eventos presenciales y online. Este repositorio contiene el código del backend, desarrollado en Java con Spring Boot y PostgreSQL.
🌐 Descripción
El backend de Loopin proporciona una API REST robusta que permite:
Crear, editar, eliminar y visualizar eventos tecnológicos.
Inscribirse y desapuntarse de eventos.
Registrar usuarios, iniciar sesión y proteger rutas mediante autenticación JWT.
Gestionar la asistencia de eventos por usuario.
Filtrar eventos por nombre, categoría, fecha o creador.
Validación de duplicados en inscripciones.
🛠 Tecnologías y herramientas
Java 21
Spring Boot
Spring Security (JWT)
PostgreSQL
Maven
Postman (para pruebas de la API)
Git & GitHub
🧱 Estructura del proyecto
bash
CopiarEditar
loopin-backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/loopinback/loopinback/
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── dto/                 # Objetos de transferencia de datos
│   │   │   ├── exception/           # Manejo de errores personalizados
│   │   │   ├── model/               # Entidades JPA
│   │   │   ├── repository/          # Interfaces de acceso a datos
│   │   │   ├── security/            # Configuración JWT y filtros de seguridad
│   │   │   └── service/             # Lógica de negocio
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
│       └── LoopinbackApplicationTests.java

🔐 Seguridad
Autenticación y autorización mediante JWT (JSON Web Token).
Filtros personalizados para proteger rutas privadas.
Roles y validación de permisos en endpoints.
✅ Requisitos funcionales implementados (Backend)
CRUD completo para eventos.
Registro, login y gestión de usuarios.
API para la asistencia a eventos.
Filtro y paginación de eventos.
Validaciones de negocio (evitar duplicación de inscripciones).
Manejo centralizado de excepciones.
Conexión con base de datos PostgreSQL.
▶️ Cómo ejecutar el proyecto
Clona el repositorio:
bash
CopiarEditar
git clone https://github.com/miskybox/Loopin-Backend.git

Importa el proyecto en tu IDE (por ejemplo, IntelliJ o Eclipse) como un proyecto Maven.
Crea una base de datos en PostgreSQL (nombre por defecto: loopin_db) y configura application.propertiescon tus credenciales:
properties
CopiarEditar
spring.datasource.url=jdbc:postgresql://localhost:5432/loopin_db
spring.datasource.username=your_username
spring.datasource.password=your_password

Ejecuta la aplicación:
bash
CopiarEditar
./mvnw spring-boot:run

La API estará disponible en:
http://localhost:8080
🔍 Endpoints principales
Método
Endpoint
Descripción
POST
/api/auth/register
Registro de usuarios
POST
/api/auth/login
Login y generación de token JWT
GET
/api/events
Listado y filtrado de eventos
POST
/api/events
Crear nuevo evento (autenticado)
POST
/api/attendances
Inscribirse a evento
DELETE
/api/attendances
Cancelar asistencia a evento
GET
/api/users/me
Perfil del usuario autenticado

🧪 Tests
Test unitario básico incluido (LoopinbackApplicationTests.java)
Pruebas adicionales con Postman (colecciones disponibles en el equipo).
👥 Equipo de desarrollo
Proyecto desarrollado por el equipo Code Crafters como parte del bootcamp FemCoders - Factoría F5:
Guadalupe Hani – Product Owner, Frontend Developer
GitHub | LinkedIn
Mariana Marin Flor – Scrum Master, Frontend Developer
GitHub | LinkedIn
Miriam Sánchez Ordoñez – Frontend Developer
GitHub | LinkedIn
Eva Sisali Guzmán – Frontend/Backend Developer
GitHub | LinkedIn
Tetiana Vashchenko – Frontend Developer
GitHub | LinkedIn
📄 Licencia
Este proyecto es de uso educativo y está bajo licencia MIT.

