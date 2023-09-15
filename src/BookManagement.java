import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

public class BookManagement {

    private JFrame loginFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private JFrame frame;
    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTable booksTable;
    private Connection connection;

    private DefaultTableModel tableModel;
    private Map<String, String> searchOptionsMap;


    public BookManagement() {
        UIManager.put("Button.background", new Color(255, 192, 203));  // 设置按钮背景颜色为粉红色
        UIManager.put("Button.foreground", Color.WHITE);  // 设置按钮前景（文本）颜色为白色
        UIManager.put("Label.foreground", Color.BLACK);  // 设置标签文本颜色为黑色
        UIManager.put("Frame.background", Color.WHITE);  // 设置窗口背景颜色为白色
        UIManager.put("Panel.background", new Color(255, 228, 225));  // 设置面板背景颜色为淡红色
        UIManager.put("TextField.background", Color.WHITE);  // 设置文本框背景颜色为白色
        UIManager.put("PasswordField.background", Color.WHITE);  // 设置密码框背景颜色为白色



        searchOptionsMap = new HashMap<>();
        searchOptionsMap.put("ID", "book_id");
        searchOptionsMap.put("书名", "book_name");
        searchOptionsMap.put("ISBN", "isbn");
        searchOptionsMap.put("作者", "author");
        searchOptionsMap.put("价格", "price");
        searchOptionsMap.put("出版社", "publisher");


        showLoginWindow();
    }

    private void showLoginWindow() {



        loginFrame = new JFrame("登录窗口");  // Translated "Login" to "登录"
        loginFrame.setSize(300, 350);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(null);

        JLabel usernameLabel = new JLabel("用户名:");  // Translated "Username:" to "用户名:"
        usernameLabel.setBounds(50, 100, 80, 25);
        loginFrame.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(130, 100, 120, 25);
        loginFrame.add(usernameField);

        JLabel passwordLabel = new JLabel("密码:");  // Translated "Password:" to "密码:"
        passwordLabel.setBounds(50, 140, 80, 25);
        loginFrame.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(130, 140, 120, 25);
        loginFrame.add(passwordField);

        loginButton = new JButton("登录");  // Translated "Login" to "登录"
        loginButton.setBounds(110, 190, 80, 30);
        loginButton.addActionListener(e -> validateLogin());
        loginFrame.add(loginButton);

        loginFrame.setVisible(true);


    }


    private void validateLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if ("123".equals(username) && "123".equals(password)) {
            // close login window and show main window
            loginFrame.dispose();
            showMainWindow();
        } else {
            JOptionPane.showMessageDialog(loginFrame, "户名或密码无效！");
        }
    }

    private void showMainWindow() {
        try {
            // Load the MySQL JDBC driver and connect to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/BookManagement?useSSL=false&serverTimezone=UTC", "root", "your_mima");//输入自己数据库的用户名和密码

            frame = new JFrame("图书管理窗口");
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(null);

            // Search field
            searchField = new JTextField();
            searchField.setBounds(10, 10, 320, 25);
            frame.add(searchField);

            // Dropdown for search options
            String[] searchOptions = {"ID", "书名", "ISBN", "作者", "价格", "出版社"};
            JComboBox<String> searchOptionsDropdown = new JComboBox<>(searchOptions);
            searchOptionsDropdown.setBounds(340, 10, 80, 25);
            frame.add(searchOptionsDropdown);

            // Search button
            searchButton = new JButton("查找");
            searchButton.setBounds(280, 260, 80, 25);
            searchButton.addActionListener(e -> searchBooks(searchOptionsDropdown.getSelectedItem().toString()));
            frame.add(searchButton);

            // Table to display books
            tableModel = new DefaultTableModel();
            booksTable = new JTable(tableModel);
            tableModel.addColumn("ID");
            tableModel.addColumn("书名");
            tableModel.addColumn("ISBN");
            tableModel.addColumn("作者");
            tableModel.addColumn("价格");
            tableModel.addColumn("出版社");
            // ... [Add other columns as required]

            JScrollPane scrollPane = new JScrollPane(booksTable);
            scrollPane.setBounds(10, 50, 560, 200);
            frame.add(scrollPane);

            // Add button
            addButton = new JButton("添加");
            addButton.setBounds(10, 260, 80, 25);
            addButton.addActionListener(e -> addBook());
            frame.add(addButton);

            // Edit button
            editButton = new JButton("修改");
            editButton.setBounds(100, 260, 80, 25);
            editButton.addActionListener(e -> editBook());
            frame.add(editButton);

            // Delete button
            deleteButton = new JButton("删除");
            deleteButton.setBounds(190, 260, 80, 25);
            deleteButton.addActionListener(e -> deleteBook());
            frame.add(deleteButton);

            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void searchBooks(String searchOption) {
        String keyword = searchField.getText().trim();
        String searchColumn = searchOptionsMap.get(searchOption);
        refreshBooksTable(searchColumn, keyword);
    }




    private void addBook() {

        JDialog addDialog = new JDialog(frame, "添加图书", true);
        addDialog.setLayout(new GridLayout(0, 2));

        JTextField bookNameField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField publisherField = new JTextField();

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            String bookName = bookNameField.getText().trim();
            String isbn = isbnField.getText().trim();
            String author = authorField.getText().trim();
            String price = priceField.getText().trim();
            String publisher = publisherField.getText().trim();

            if (!bookName.isEmpty() && !isbn.isEmpty() && !author.isEmpty() && !price.isEmpty() && !publisher.isEmpty()) {
                try {
                    String sql = "INSERT INTO books (book_name, isbn, author, price, publisher) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, bookName);
                    stmt.setString(2, isbn);
                    stmt.setString(3, author);
                    stmt.setString(4, price);
                    stmt.setString(5, publisher);
                    stmt.executeUpdate();
                    stmt.close();
                    JOptionPane.showMessageDialog(frame, "图书添加成功!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "添加图书出错，请重试。");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "请填写所有字段。");
            }
            addDialog.dispose();
            refreshBooksTable(null);
        });

        addDialog.add(new JLabel("书名:"));
        addDialog.add(bookNameField);
        addDialog.add(new JLabel("ISBN:"));
        addDialog.add(isbnField);
        addDialog.add(new JLabel("作者:"));
        addDialog.add(authorField);
        addDialog.add(new JLabel("价格:"));
        addDialog.add(priceField);
        addDialog.add(new JLabel("出版社:"));
        addDialog.add(publisherField);
        addDialog.add(new JLabel()); // Empty label for spacing
        addDialog.add(saveButton);

        addDialog.pack();
        addDialog.setLocationRelativeTo(frame);
        addDialog.setVisible(true);
    }


    private void editBook() {
        if (booksTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(frame, "请先选择要编辑的书籍！");  // Translated to "Please select a book to edit!"
            return;
        }

        int selectedBookId = (int) booksTable.getValueAt(booksTable.getSelectedRow(), 0);

        JDialog editDialog = new JDialog(frame, "编辑书籍", true);  // Translated "Edit Book" to "编辑书籍"
        editDialog.setLayout(new GridLayout(0, 2));

        JTextField bookNameField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField publisherField = new JTextField();

        // Load the book details from the database
        try {
            String query = "SELECT * FROM books WHERE book_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, selectedBookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                bookNameField.setText(rs.getString("book_name"));
                isbnField.setText(rs.getString("isbn"));
                authorField.setText(rs.getString("author"));
                priceField.setText(rs.getString("price"));
                publisherField.setText(rs.getString("publisher"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("保存");  // Translated "Save" to "保存"
        saveButton.addActionListener(e -> {
            try {
                String updateSql = "UPDATE books SET book_name = ?, isbn = ?, author = ?, price = ?, publisher = ? WHERE book_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setString(1, bookNameField.getText().trim());
                updateStmt.setString(2, isbnField.getText().trim());
                updateStmt.setString(3, authorField.getText().trim());
                updateStmt.setBigDecimal(4, new BigDecimal(priceField.getText().trim()));  // Assuming price is stored as DECIMAL in the database
                updateStmt.setString(5, publisherField.getText().trim());
                updateStmt.setInt(6, selectedBookId);
                updateStmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "书籍更新成功！");  // Translated "Book updated successfully!" to "书籍更新成功！"
                refreshBooksTable(null);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "更新书籍时出错。请重试。");  // Translated "Error updating book. Please try again." to "更新书籍时出错。请重试。"
            }
            editDialog.dispose();
        });

        editDialog.add(new JLabel("书名:"));  // Book Name
        editDialog.add(bookNameField);
        editDialog.add(new JLabel("ISBN:"));
        editDialog.add(isbnField);
        editDialog.add(new JLabel("作者:"));  // Author
        editDialog.add(authorField);
        editDialog.add(new JLabel("价格:"));  // Price
        editDialog.add(priceField);
        editDialog.add(new JLabel("出版社:"));  // Publisher
        editDialog.add(publisherField);
        editDialog.add(new JLabel());
        editDialog.add(saveButton);

        editDialog.pack();
        editDialog.setLocationRelativeTo(frame);
        editDialog.setVisible(true);
    }



    private void deleteBook() {
        if (booksTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(frame, "请选择要删除的账簿！");
            return;
        }

        int selectedBookId = (int) booksTable.getValueAt(booksTable.getSelectedRow(), 0);
        int choice = JOptionPane.showConfirmDialog(frame, "您确定要删除所选图书吗？", "删除确认", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                String deleteSql = "DELETE FROM books WHERE book_id = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                deleteStmt.setInt(1, selectedBookId);
                deleteStmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, "书籍删除成功！");
                refreshBooksTable(null);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "删除书本时出错。请重试。");
            }
        }
    }



    private void refreshBooksTable(String keyword) {
        // Clear the existing table rows
        tableModel.setRowCount(0);

        // Query the database based on the keyword
        String query = "SELECT * FROM books";
        if (keyword != null && !keyword.isEmpty()) {
            query += " WHERE book_id = ? OR book_name = ? OR isbn = ? OR author = ? OR price = ? OR publisher = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (keyword != null && !keyword.isEmpty()) {
                stmt.setString(1, keyword);
                stmt.setString(2, keyword);
                stmt.setString(3, keyword);
                stmt.setString(4, keyword);
                stmt.setString(5, keyword);
                stmt.setString(6, keyword);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getString("isbn"),
                        rs.getString("author"),
                        rs.getBigDecimal("price"),
                        rs.getString("publisher")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshBooksTable(String searchColumn, String keyword) {
        // Clear the existing table rows
        tableModel.setRowCount(0);

        // Query the database based on the searchColumn and keyword
        String query = "SELECT * FROM books";
        if (searchColumn != null && !searchColumn.isEmpty() && keyword != null && !keyword.isEmpty()) {
            query += " WHERE " + searchColumn + " = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (searchColumn != null && !searchColumn.isEmpty() && keyword != null && !keyword.isEmpty()) {
                stmt.setString(1, keyword);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("book_name"),
                        rs.getString("isbn"),
                        rs.getString("author"),
                        rs.getBigDecimal("price"),
                        rs.getString("publisher")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        new BookManagement();
    }
}
