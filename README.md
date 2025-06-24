# 🐟 Atunes del Pacífico S.A. - Backend API

## 📋 Descripción del Proyecto

Sistema de gestión integral para la empresa **Atunes del Pacífico S.A.**, desarrollado con **Spring Boot** que permite administrar las operaciones completas desde la producción y almacenamiento de productos de atún hasta la distribución y ventas.

### 🎯 Objetivos Principales
- ✅ Control completo del inventario de producción
- ✅ Gestión eficiente de pedidos y clientes
- ✅ Monitoreo de lotes de producción con trazabilidad
- ✅ Sistema de roles y autenticación segura
- ✅ Generación de reportes ejecutivos
- ✅ API REST documentada con Swagger

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17 | Lenguaje de programación |
| **Spring Boot** | 3.5.0 | Framework principal |
| **Spring Security** | 6.x | Autenticación y autorización |
| **Spring Data JPA** | 3.x | Persistencia de datos |
| **JWT** | 0.11.5 | Tokens de autenticación |
| **MySQL** | 8.0 | Base de datos |
| **Swagger/OpenAPI** | 2.6.0 | Documentación de API |
| **Lombok** | - | Reducción de código boilerplate |
| **Maven** | 3.x | Gestión de dependencias |

---

## 🗄️ Estructura de Base de Datos

### Diagrama de Entidades Principales

```mermaid
erDiagram
    ROL ||--o{ USUARIO : tiene
    USUARIO ||--o{ CLIENTE : es
    USUARIO ||--o{ PEDIDO : crea
    CLIENTE ||--o{ PEDIDO : realiza
    PRODUCTO ||--o{ LOTE : tiene
    LOTE ||--o{ DETALLE_PEDIDO : contiene
    PEDIDO ||--o{ DETALLE_PEDIDO : incluye
    
    ROL {
        tinyint id_rol PK
        varchar nombre
        varchar descripcion
        boolean activo
    }
    
    USUARIO {
        bigint id_usuario PK
        varchar nombre_usuario
        varchar contrasena
        varchar correo
        tinyint rol_id FK
        boolean activo
    }
    
    CLIENTE {
        bigint id_usuario PK,FK
        enum tipo
        varchar nombre
        varchar identificacion
        varchar telefono
        varchar direccion
        enum estado
    }
    
    PRODUCTO {
        bigint id_producto PK
        varchar codigo_sku
        varchar nombre
        varchar descripcion
        enum conservante
        int contenido_g
        decimal precio_lista
        decimal precio_costo
        int stock_minimo
    }
    
    LOTE {
        bigint id_lote PK
        varchar codigo_lote
        bigint producto_id FK
        date fecha_prod
        date fecha_venc
        int cantidad_total
        int cantidad_disp
        enum estado
        varchar ubicacion
    }
    
    PEDIDO {
        bigint id_pedido PK
        varchar numero_pedido
        bigint cliente_id FK
        datetime fecha_pedido
        date fecha_entrega
        enum estado
        decimal subtotal
        decimal impuestos
        decimal total
        enum metodo_pago
        bigint created_by FK
    }
    
    DETALLE_PEDIDO {
        bigint id_detalle PK
        bigint pedido_id FK
        bigint lote_id FK
        int cantidad
        decimal precio_unitario
        decimal subtotal
    }
```

### 📊 Tablas Principales

#### 🔐 **Gestión de Usuarios**
- **`rol`**: Roles del sistema (Cliente, Operador, Administrador)
- **`usuario`**: Usuarios con autenticación JWT
- **`cliente`**: Información extendida de clientes

#### 📦 **Gestión de Productos**
- **`producto`**: Catálogo de productos (Atún en Aceite, Agua, Salsa)
- **`lote`**: Lotes de producción con trazabilidad completa

#### 🛒 **Gestión de Pedidos**
- **`pedido`**: Órdenes de compra de clientes
- **`detalle_pedido`**: Productos específicos por pedido

#### 📋 **Auditoría**
- **`auditoria`**: Registro completo de operaciones del sistema

---

## 🚀 Instalación y Configuración

### 📋 Prerrequisitos
- ☕ **Java 17** o superior
- 🔧 **Maven 3.6+**
- 🗄️ **MySQL 8.0+** (o acceso a base de datos en la nube)
- 🌐 **Git**

### 🔧 Configuración del Entorno

#### 1. **Clonar el Repositorio**
```bash
git clone https://github.com/EmmanuelSan01/Proyecto_BACK_SpringBoot_PereiraDanielSanchezEmmanuel.git
cd Proyecto_BACK_SpringBoot_PereiraDanielSanchezEmmanuel
```

#### 2. **Configurar Base de Datos**

Editar `src/main/resources/application.properties`:

```properties
# Configuración de Base de Datos
spring.datasource.url=jdbc:mysql://tu-servidor:puerto/atunes_del_pacifico
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Configuración JWT
jwt.secret=tu_clave_secreta_muy_segura_aqui
jwt.expiration=86400000

# Configuración CORS (incluir URLs de producción)
cors.allowed-origins=http://localhost:3000,http://localhost:8081,https://atunesdelpacifico.netlify.app

# Puerto del servidor
server.port=8080
server.servlet.context-path=/api
```

#### 3. **Ejecutar la Aplicación**

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar la aplicación
mvn spring-boot:run
```

#### 4. **Verificar Instalación**

##### 🏠 **Desarrollo**
- 🌐 **API Base**: `http://localhost:8080/api`
- 📚 **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- 📖 **API Docs**: `http://localhost:8080/api/v3/api-docs`

##### 🌐 **Producción**
- 🌐 **API Base**: `https://atunesdelpacifico.onrender.com/`
- 📚 **Swagger UI**: `https://atunesdelpacifico.onrender.com/swagger-ui.html`
- 🖥️ **Frontend**: `https://atunesdelpacifico.netlify.app/`

---

## 🔐 Sistema de Autenticación

### 🎭 Roles del Sistema

| Rol | Permisos | Descripción |
|-----|----------|-------------|
| **👤 CLIENTE** | Realizar pedidos, consultar historial | Usuario final que compra productos |
| **👨‍💼 OPERADOR** | Gestionar inventario, supervisar pedidos | Personal operativo de la empresa |
| **👑 ADMINISTRADOR** | Acceso completo al sistema | Administrador con todos los permisos |

### 🔑 Autenticación JWT

#### **Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "nombreUsuario": "admin_sistema",
  "contrasena": "admin123"
}
```

#### **Respuesta**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "nombreUsuario": "admin_sistema",
  "correo": "admin@empresa.com",
  "rol": "ADMINISTRADOR"
}
```

#### **Uso del Token**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📡 Endpoints de la API

### 🔐 **Autenticación** (`/auth`)

| Método | Endpoint | Descripción | Acceso |
|--------|----------|-------------|---------|
| POST | `/auth/login` | Iniciar sesión | Público |
| POST | `/auth/register` | Registrar usuario | Público |

### 👥 **Usuarios** (`/usuarios`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/usuarios` | Listar usuarios | ADMINISTRADOR |
| GET | `/usuarios/{id}` | Obtener usuario | ADMINISTRADOR |
| POST | `/usuarios` | Crear usuario | ADMINISTRADOR |
| PUT | `/usuarios/{id}` | Actualizar usuario | ADMINISTRADOR |
| DELETE | `/usuarios/{id}` | Eliminar usuario | ADMINISTRADOR |

### 👤 **Clientes** (`/clientes`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/clientes` | Listar clientes | OPERADOR+ |
| GET | `/clientes/{id}` | Obtener cliente | OPERADOR+ |
| POST | `/clientes` | Crear cliente | OPERADOR+ |
| PUT | `/clientes/{id}` | Actualizar cliente | OPERADOR+ |
| GET | `/clientes/{id}/pedidos` | Historial de pedidos | OPERADOR+ |

### 📦 **Productos** (`/productos`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/productos` | Listar productos | Público |
| GET | `/productos/{id}` | Obtener producto | Público |
| POST | `/productos` | Crear producto | OPERADOR+ |
| PUT | `/productos/{id}` | Actualizar producto | OPERADOR+ |
| DELETE | `/productos/{id}` | Eliminar producto | ADMINISTRADOR |

### 🏭 **Lotes** (`/lotes`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/lotes` | Listar lotes | OPERADOR+ |
| GET | `/lotes/{id}` | Obtener lote | OPERADOR+ |
| POST | `/lotes` | Crear lote | OPERADOR+ |
| PUT | `/lotes/{id}` | Actualizar lote | OPERADOR+ |
| GET | `/lotes/producto/{id}` | Lotes por producto | OPERADOR+ |

### 🛒 **Pedidos** (`/pedidos`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/pedidos` | Listar pedidos | OPERADOR+ |
| GET | `/pedidos/{id}` | Obtener pedido | CLIENTE+ |
| POST | `/pedidos` | Crear pedido | CLIENTE+ |
| PUT | `/pedidos/{id}` | Actualizar pedido | OPERADOR+ |
| PUT | `/pedidos/{id}/estado` | Cambiar estado | OPERADOR+ |

### 📊 **Reportes** (`/reportes`)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/reportes/dashboard` | Estadísticas generales | OPERADOR+ |
| GET | `/reportes/ventas` | Reporte de ventas | OPERADOR+ |
| GET | `/reportes/inventario` | Reporte de inventario | OPERADOR+ |
| GET | `/reportes/clientes` | Reporte de clientes | OPERADOR+ |

---

## 📝 Ejemplos de Uso

### 🛒 **Crear un Pedido**

```http
POST /api/pedidos
Authorization: Bearer {token}
Content-Type: application/json

{
  "clienteId": 1,
  "fechaEntrega": "2025-07-01",
  "metodoPago": "TRANSFERENCIA",
  "detalles": [
    {
      "loteId": 1,
      "cantidad": 5,
      "precioUnitario": 2.85
    },
    {
      "loteId": 2,
      "cantidad": 3,
      "precioUnitario": 2.19
    }
  ]
}
```

### 📦 **Registrar Nuevo Lote**

```http
POST /api/lotes
Authorization: Bearer {token}
Content-Type: application/json

{
  "codigoLote": "L2025-001-ATN",
  "productoId": 1,
  "fechaProduccion": "2025-06-24",
  "fechaVencimiento": "2027-06-24",
  "cantidadTotal": 2000,
  "ubicacion": "Bodega A-1"
}
```

### 👤 **Registrar Cliente**

```http
POST /api/clientes
Authorization: Bearer {token}
Content-Type: application/json

{
  "tipo": "EMPRESA",
  "nombre": "Distribuidora del Norte S.A.",
  "identificacion": "900123456-7",
  "correo": "contacto@distribuidoranorte.com",
  "telefono": "+57300123456",
  "direccion": "Calle 100 #15-30, Bogotá"
}
```

---

## 🔒 Configuración de Seguridad

### 🛡️ **Spring Security**

La aplicación implementa seguridad robusta con:

- ✅ **Autenticación JWT** sin estado (stateless)
- ✅ **Autorización basada en roles**
- ✅ **Cifrado de contraseñas** con BCrypt
- ✅ **CORS configurado** para frontend
- ✅ **Endpoints públicos** para productos
- ✅ **Protección CSRF** deshabilitada para API REST

### 🔐 **Configuración JWT**

```properties
# Clave secreta (cambiar en producción)
jwt.secret=mySecretKeyForAtunesDelPacificoSASystemVerySecureAndLong2025

# Tiempo de expiración (24 horas)
jwt.expiration=86400000
```

### 🌐 **Configuración CORS**

```properties
# Orígenes permitidos
cors.allowed-origins=http://localhost:3000,http://localhost:8081,https://atunesdelpacifico.netlify.app
```

---

## 📊 Base de Datos en la Nube

### 🌩️ **TiDB Cloud Configuration**

La aplicación está configurada para usar **TiDB Cloud** (compatible con MySQL):

```properties
spring.datasource.url=jdbc:mysql://gateway01.us-east-1.prod.aws.tidbcloud.com:4000/atunes_del_pacifico
spring.datasource.username=sp1y8GUVRhdTTfC.root
spring.datasource.password=5URlRNUAcB2jYWxe
```

### 📋 **Datos de Prueba**

La base de datos incluye datos de prueba:
- 👥 **3 roles** del sistema
- 👤 **24 usuarios** con diferentes roles
- 🏢 **19 clientes** (personas naturales y empresas)
- 📦 **3 productos** de atún
- 🏭 **5 lotes** de producción
- 🛒 **33 pedidos** con sus detalles

---

## 🚀 Despliegue

### 🐳 **Docker (Opcional)**

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/atunesdelpacifico-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.war"]
```

### 🌐 **URLs de Producción**

La aplicación está desplegada y disponible en:

#### 🔗 **Backend API**
- **URL**: https://atunesdelpacifico.onrender.com/
- **Swagger**: https://atunesdelpacifico.onrender.com/swagger-ui.html
- **Health Check**: https://atunesdelpacifico.onrender.com/actuator/health

#### 🖥️ **Frontend Web**
- **URL**: https://atunesdelpacifico.netlify.app/
- **Repositorio**: https://github.com/AtexDEPR/Proyecto_FRONT_SpringBoot_PereiraDanielSanchezEmmanuel

#### 📂 **Repositorios**
- **Backend**: https://github.com/EmmanuelSan01/Proyecto_BACK_SpringBoot_PereiraDanielSanchezEmmanuel
- **Frontend**: https://github.com/AtexDEPR/Proyecto_FRONT_SpringBoot_PereiraDanielSanchezEmmanuel

### ☁️ **Despliegue en la Nube**

La aplicación está optimizada para despliegue en:
- ☁️ **AWS** (Elastic Beanstalk, EC2)
- 🌐 **Heroku**
- 🔵 **Azure** App Service
- 🟢 **Google Cloud** Platform

### 🔧 **Variables de Entorno**

```bash
export DB_URL=jdbc:mysql://tu-servidor:puerto/atunes_del_pacifico
export DB_USERNAME=tu_usuario
export DB_PASSWORD=tu_contraseña
export JWT_SECRET=tu_clave_secreta
export CORS_ORIGINS=https://atunesdelpacifico.netlify.app,http://localhost:3000
```

---

## 📚 Documentación Adicional

### 📖 **Swagger/OpenAPI**

La API está completamente documentada con Swagger:

#### 🏠 **Desarrollo**
- 🌐 **URL**: `http://localhost:8080/api/swagger-ui.html`
- 📄 **JSON**: `http://localhost:8080/api/v3/api-docs`

#### 🌐 **Producción**
- 🌐 **URL**: `https://atunesdelpacifico.onrender.com/swagger-ui.html`
- 📄 **JSON**: `https://atunesdelpacifico.onrender.com/v3/api-docs`

### 🧪 **Testing**

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report

# Tests de integración
mvn verify
```

### 📊 **Monitoreo**

La aplicación incluye endpoints de monitoreo:

```http
GET /api/actuator/health
GET /api/actuator/info
GET /api/actuator/metrics
```

---

## 🤝 Contribución

### 📋 **Guías de Contribución**

1. 🍴 Fork el repositorio
2. 🌿 Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. 💾 Commit cambios (`git commit -am 'Agregar nueva funcionalidad'`)
4. 📤 Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. 🔄 Crear Pull Request

```bash
git clone https://github.com/EmmanuelSan01/Proyecto_BACK_SpringBoot_PereiraDanielSanchezEmmanuel.git
cd Proyecto_BACK_SpringBoot_PereiraDanielSanchezEmmanuel
```

### 📝 **Estándares de Código**

- ☕ **Java Code Conventions**
- 🏗️ **Clean Architecture**
- 📋 **SOLID Principles**
- 🧪 **Test-Driven Development**

---

## 📞 Soporte y Contacto

### 🆘 **Soporte Técnico**

- 📧 **Email**: soporte@atunesdelpacifico.com
- 📱 **WhatsApp**: +57 300 123 4567
- 🌐 **Website**: https://atunesdelpacifico.com

### 👨‍💻 **Equipo de Desarrollo**

- **Desarrollador Backend**: Emmanuel Sanchez
- **Desarrollador Frontend**: Daniel Pereira
- **GitHub Backend**: [@EmmanuelSan01](https://github.com/EmmanuelSan01)
- **GitHub Frontend**: [@AtexDEPR](https://github.com/AtexDEPR)

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

---

## 🔄 Changelog

### v1.0.0 (2025-06-24)
- ✅ Implementación inicial del sistema
- ✅ Autenticación JWT completa
- ✅ CRUD completo para todas las entidades
- ✅ Sistema de roles y permisos
- ✅ Documentación Swagger
- ✅ Base de datos en la nube
- ✅ Datos de prueba incluidos

---

**🐟 Atunes del Pacífico S.A. - Sistema de Gestión Integral**

*Desarrollado con ❤️ en Campuslands 👨‍🚀*
