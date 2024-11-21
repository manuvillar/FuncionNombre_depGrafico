package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FuncNombreSwing {
    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame frame = new JFrame("Consultar Nombre de Departamento");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new BorderLayout());

        // Crear el panel para la entrada de datos
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Número de Departamento:");
        JTextField deptField = new JTextField(10);
        JButton consultarButton = new JButton("Consultar");
        inputPanel.add(label);
        inputPanel.add(deptField);
        inputPanel.add(consultarButton);

        // Crear el área de texto para mostrar el resultado
        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Añadir los componentes a la ventana
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Añadir funcionalidad al botón
        consultarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String depInput = deptField.getText().trim();
                if (!depInput.matches("\\d+")) {
                    JOptionPane.showMessageDialog(frame, "Por favor, introduce un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int depNumber = Integer.parseInt(depInput);
                try {
                    // Conectar a la base de datos
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conexion = DriverManager.getConnection(
                            "jdbc:mysql://localhost/practica", "root", "practica");

                    // Preparar la llamada a la función almacenada
                    String sql = "{ ? = call nombre_dep(?) }";
                    CallableStatement llamada = conexion.prepareCall(sql);

                    // Registrar parámetros
                    llamada.registerOutParameter(1, Types.VARCHAR); // Salida
                    llamada.setInt(2, depNumber); // Entrada

                    // Ejecutar la llamada
                    llamada.executeUpdate();
                    String result = llamada.getString(1);

                    // Mostrar el resultado
                    resultArea.setText("Resultado:\n");
                    resultArea.append("Nombre del Departamento: " + result);

                    llamada.close();
                    conexion.close();
                } catch (ClassNotFoundException cn) {
                    resultArea.setText("Error: No se encontró el driver de la base de datos.");
                    cn.printStackTrace();
                } catch (SQLException ex) {
                    resultArea.setText("Error de conexión o consulta:\n" + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Mostrar la ventana
        frame.setVisible(true);
    }
}
