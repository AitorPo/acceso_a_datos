package util;

public class Constants {
    public static class ConstantsCiudad {
        public static final String INSERT = "INSERT INTO ciudad (nombre, ccaa) VALUES (?, ?)";
        public static final String DELETE = "DELETE FROM ciudad WHERE nombre = ?";
        public static final String UPDATE = "UPDATE ciudad SET nombre = ? WHERE id_ciudad = ?";
        public static final String EXISTS = "SELECT * FROM ciudad WHERE nombre = ?";
        public static final String SELECT_NAME = "SELECT nombre FROM ciudad";
        public static final String SELECT_ID_BY_NAME = "SELECT id_ciudad FROM ciudad WHERE nombre LIKE ?";
    }

    public static class ConstantsParque{
        public static final String INSERT = "INSERT INTO parque (nombre, id_ciudad) VALUES (?, ?)";
        public static final String DELETE = "DELETE p.*, t.* FROM parque p LEFT JOIN tarea t ON p.id_parque = t.id_parque WHERE p.id_parque = ?";
        public static final String UPDATE = "UPDATE parque SET nombre = ?, id_ciudad = ? WHERE id_parque = ?";
        public static final String EXISTS = "SELECT * FROM parque WHERE nombre = ?";
        public static final String SELECT_All = "SELECT * FROM parque";
        public static final String SELECT_NAME_BY_ID = "SELECT c.nombre FROM ciudad c INNER JOIN parque p ON c.id_ciudad = p.id_ciudad WHERE p.id_ciudad = ?";
    }

    public static class ConstantsTarea{
        public static final String INSERT = "INSERT INTO tarea (id_parque, nombre, descripcion) VALUES (?, ?, ?)";
        public static final String DELETE = "DELETE FROM tarea WHERE nombre = ?";
        public static final String UPDATE = "UPDATE tarea SET nombre = ?, descripcion = ? WHERE id_parque = ?";
        public static final String EXISTS = "SELECT * FROM tarea WHERE nombre = ?";
        public static final String SELECT_ALL = "SELECT * FROM tarea";
    }

    public static class ConstantsOperario{
        public static final String INSERT = "INSERT INTO operario (nombre, password) VALUES (?, ?)";
        public static final String DELETE = "DELETE FROM operario WHERE nombre = ?";
        public static final String UPDATE = "UPDATE operario SET nombre = ?, password = ? WHERE id_operario = ?";
        public static final String EXISTS = "SELECT COUNT(*) FROM operario WHERE nombre = ?";
        public static final String SELECT_ALL = "SELECT * FROM operario";


    }
}
