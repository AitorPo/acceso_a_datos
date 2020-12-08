package util;

public class Constants {
    public static class ConstantsCiudad {
        public static final String INSERT = "INSERT INTO ciudad (nombre, ccaa) VALUES (?, ?)";
        public static final String DELETE = "DELETE from ciudad WHERE nombre = ?";
        public static final String UPDATE = "UPDATE ciudad SET nombre = ? where id_ciudad = ?";
        public static final String EXISTS = "SELECT * FROM ciudad WHERE nombre = ?";
        public static final String SELECT_ALL = "SELECT * FROM ciudad";
    }

    public static class ConstantsParque{
        public static final String INSERT = "INSERT INTO parque (nombre) VALUES (?, ?)";
        public static final String DELETE = "DELETE from parque WHERE nombre = ?";
        public static final String UPDATE = "UPDATE parque SET nombre = ? where id_parque = ?";
        public static final String EXISTS = "SELECT * FROM parque WHERE nombre = ?";
        public static final String SELECT_ALL = "SELECT * FROM parque";
    }

    public static class ConstantsTarea{
        public static final String INSERT = "INSERT INTO tarea (id_parque, nombre, descripcion) VALUES (?, ?, ?)";
        public static final String DELETE = "DELETE from tarea WHERE nombre = ?";
        public static final String UPDATE = "UPDATE tarea SET nombre = ? where id_parque = ?";
        public static final String EXISTS = "SELECT * FROM tarea WHERE nombre = ?";
        public static final String SELECT_ALL = "SELECT * FROM tarea";
    }
}
