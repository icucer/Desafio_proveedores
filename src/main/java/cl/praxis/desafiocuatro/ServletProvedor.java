package cl.praxis.desafiocuatro;

import cl.praxis.desafiocuatro.conexion.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ServletProvedor", value = "/ServletProvedor")
public class ServletProvedor extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Recuperar todos los proveedores de la base de datos
            List<String[]> suppliers = retrieveSuppliers();

            // Establecer los proveedores como atributo de la solicitud
            request.setAttribute("suppliers", suppliers);

            // Redirigir a la página JSP para mostrar la tabla con todos los proveedores
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new RuntimeException("Error al procesar la solicitud", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String[] parameters = {"name", "rut", "direction", "email", "phone", "contact", "contact_phone_number"};
        String[] values = new String[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            values[i] = request.getParameter(parameters[i]);
            String error = validateParameter(values[i], parameters[i]);
            if (error != null) {
                request.setAttribute("error", error);
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }
        }

        String name = values[0];
        String rut = values[1];
        String direction = values[2];
        String email = values[3];
        String phone = values[4];
        String contact = values[5];
        String contact_phone_number = values[6];

        try {
            // Insertar el nuevo proveedor en la base de datos
            insertSupplier(name, rut, direction, email, phone, contact, contact_phone_number);

            // Recuperar todos los proveedores de la base de datos
            List<String[]> suppliers = retrieveSuppliers();

            // Establecer los proveedores como atributo de la solicitud
            request.setAttribute("suppliers", suppliers);

            // Redirigir a la página JSP para mostrar la tabla con todos los proveedores
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new RuntimeException("Error al procesar la solicitud", e);
        }
    }

    private String validateParameter(String valor, String nombreParametro) {
        if (valor == null || valor.trim().isEmpty()) {
            return nombreParametro + " es requerido";
        }
        return null;
    }

    private void insertSupplier(String name, String rut, String address, String email, String phone, String contact, String contact_phone_number) throws SQLException {
        String query = "INSERT INTO provedores (nombre, rut, direccion, correo, telefono, contacto, telefono_contacto) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Conexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name);
            statement.setString(2, rut);
            statement.setString(3, address);
            statement.setString(4, email);
            statement.setString(5, phone);
            statement.setString(6, contact);
            statement.setString(7, contact_phone_number);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String[]> retrieveSuppliers() throws SQLException {
        String query = "SELECT * FROM provedores ORDER BY nombre ASC";
        List<String[]> suppliers = new ArrayList<>();

        try (Connection connection = Conexion.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String[] supplier = new String[8];
                supplier[0] = String.valueOf(resultSet.getInt("id"));
                supplier[1] = resultSet.getString("nombre");
                supplier[2] = resultSet.getString("rut");
                supplier[3] = resultSet.getString("direccion");
                supplier[4] = resultSet.getString("correo");
                supplier[5] = resultSet.getString("telefono");
                supplier[6] = resultSet.getString("contacto");
                supplier[7] = resultSet.getString("telefono_contacto");

                suppliers.add(supplier);
            }
        }
        return suppliers;
    }
}