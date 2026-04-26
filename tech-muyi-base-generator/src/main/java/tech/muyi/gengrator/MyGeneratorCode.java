package tech.muyi.gengrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.BorderFactory;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.border.EmptyBorder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 代码生成器桌面入口。
 *
 * <p>提供可视化配置、数据库表加载与多模板批量生成能力，并把用户配置持久化到本地文件。</p>
 */
public class MyGeneratorCode {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".tech-muyi-generator-config.json");
    private static final int UI_FONT_SIZE = 24;
    private static volatile boolean browsingPath = false;

    private static final Color BG_COLOR = new Color(245, 248, 252);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color LIST_BG = new Color(250, 250, 250);
    private static final Color ACCENT_COLOR = new Color(44, 123, 229);
    private static final Color ACCENT_DARK = new Color(23, 94, 178);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyGeneratorCode::createAndShowUI);
    }

    private static void createAndShowUI() {
        setGlobalUIFont();
        GeneratorConfig config = loadConfig();
        JFrame frame = new JFrame("MyBatis 代码生成器");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(980, 720);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG_COLOR);

        JTextField projectNameField = new JTextField(config.projectName, 30);
        JTextField pathField = new JTextField(config.path, 30);
        JTextField dbHostField = new JTextField(config.dbHost, 30);
        JTextField dbPortField = new JTextField(config.dbPort, 30);
        JTextField dbNameField = new JTextField(config.dbName, 30);
        JTextField usernameField = new JTextField(config.username, 30);
        JPasswordField passwordField = new JPasswordField(config.password, 30);
        JTextField groupIdField = new JTextField(config.groupId, 30);
        JCheckBox overwriteBox = new JCheckBox("覆盖已存在文件", config.overwrite);
        JCheckBox disableOpenDirBox = new JCheckBox("生成后不打开目录", Boolean.TRUE.equals(config.disableOpenDir));
        JCheckBox multiSelectBox = new JCheckBox("多选表", config.multiSelect);
        styleCheckBox(overwriteBox);
        styleCheckBox(disableOpenDirBox);
        styleCheckBox(multiSelectBox);

        // 需要生成的模板（默认全部勾选）
        JCheckBox controllerBox = new JCheckBox("Controller", Boolean.TRUE.equals(config.genController));
        JCheckBox queryBox = new JCheckBox("Query", Boolean.TRUE.equals(config.genQuery));
        JCheckBox dtoBox = new JCheckBox("Dto", Boolean.TRUE.equals(config.genDto));
        JCheckBox daoBox = new JCheckBox("Dao", Boolean.TRUE.equals(config.genDao));
        JCheckBox doBox = new JCheckBox("Do", Boolean.TRUE.equals(config.genDo));
        JCheckBox apiserviceBox = new JCheckBox("ApiService", Boolean.TRUE.equals(config.genApiService));
        JCheckBox apiServiceImplBox = new JCheckBox("ApiServiceImpl", Boolean.TRUE.equals(config.genApiServiceImpl));
        JCheckBox serviceBox = new JCheckBox("Service", Boolean.TRUE.equals(config.genService));
        JCheckBox serviceImplBox = new JCheckBox("ServiceImpl", Boolean.TRUE.equals(config.genServiceImpl));
        JCheckBox mapperBox = new JCheckBox("Mapper", Boolean.TRUE.equals(config.genMapper));
        JCheckBox managerBox = new JCheckBox("Manager", Boolean.TRUE.equals(config.genManager));
        styleCheckBox(controllerBox);
        styleCheckBox(queryBox);
        styleCheckBox(dtoBox);
        styleCheckBox(daoBox);
        styleCheckBox(doBox);
        styleCheckBox(apiserviceBox);
        styleCheckBox(apiServiceImplBox);
        styleCheckBox(serviceBox);
        styleCheckBox(serviceImplBox);
        styleCheckBox(mapperBox);
        styleCheckBox(managerBox);

        DefaultListModel<String> tableModel = new DefaultListModel<>();
        JList<String> tableList = new JList<>(tableModel);
        tableList.setVisibleRowCount(18);
        tableList.setSelectionMode(config.multiSelect ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
        styleList(tableList);
        if (config.selectedTables != null && !config.selectedTables.isEmpty()) {
            for (String selectedTable : config.selectedTables) {
                tableModel.addElement(selectedTable);
            }
            int[] indexes = new int[config.selectedTables.size()];
            for (int i = 0; i < config.selectedTables.size(); i++) {
                indexes[i] = i;
            }
            tableList.setSelectedIndices(indexes);
        }

        JButton testButton = new JButton("测试连接");
        JButton loadTableButton = new JButton("加载表");
        JButton saveButton = new JButton("保存配置");
        JButton generateButton = new JButton("开始生成");
        JButton browsePathButton = new JButton("浏览...");
        styleButton(testButton);
        styleButton(loadTableButton);
        styleButton(saveButton);
        styleButton(generateButton);
        styleButton(browsePathButton);

        multiSelectBox.addActionListener(e -> {
            int mode = multiSelectBox.isSelected() ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION;
            tableList.setSelectionMode(mode);
            if (!multiSelectBox.isSelected() && tableList.getSelectedIndices().length > 1) {
                tableList.setSelectedIndex(tableList.getSelectedIndices()[0]);
            }
        });

        testButton.addActionListener(e -> {
            String url = buildJdbcUrl(dbHostField.getText().trim(), dbPortField.getText().trim(), dbNameField.getText().trim());
            String testResult = testConnection(url, usernameField.getText().trim(), new String(passwordField.getPassword()));
            JOptionPane.showMessageDialog(frame, testResult);
        });

        loadTableButton.addActionListener(e -> {
            String url = buildJdbcUrl(dbHostField.getText().trim(), dbPortField.getText().trim(), dbNameField.getText().trim());
            List<String> tables = queryTables(url, usernameField.getText().trim(), new String(passwordField.getPassword()));
            tableModel.clear();
            for (String table : tables) {
                tableModel.addElement(table);
            }
            restoreSelectedTables(tableList, tables, config.selectedTables);
            JOptionPane.showMessageDialog(frame, "已加载 " + tables.size() + " 张表");
        });

        saveButton.addActionListener(e -> {
            GeneratorConfig current = buildConfig(projectNameField, pathField, dbHostField, dbPortField, dbNameField, usernameField, passwordField, groupIdField,
                    overwriteBox, disableOpenDirBox, multiSelectBox,
                    controllerBox, queryBox, dtoBox, daoBox, doBox, apiserviceBox, apiServiceImplBox, serviceBox, serviceImplBox, mapperBox, managerBox,
                    tableList);
            saveConfig(current);
            JOptionPane.showMessageDialog(frame, "配置已保存到：" + CONFIG_PATH);
        });

        generateButton.addActionListener(e -> {
            GeneratorConfig current = buildConfig(projectNameField, pathField, dbHostField, dbPortField, dbNameField, usernameField, passwordField, groupIdField,
                    overwriteBox, disableOpenDirBox, multiSelectBox,
                    controllerBox, queryBox, dtoBox, daoBox, doBox, apiserviceBox, apiServiceImplBox, serviceBox, serviceImplBox, mapperBox, managerBox,
                    tableList);
            if (current.selectedTables.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请至少选择一张表");
                return;
            }
            List<String> existingFiles = detectExistingFiles(current);
            if (!existingFiles.isEmpty() && !current.overwrite) {
                JOptionPane.showMessageDialog(frame, "检测到已有文件，且当前未开启覆盖：\n" + String.join("\n", existingFiles));
                return;
            }
            if (!existingFiles.isEmpty() && current.overwrite) {
                int confirm = JOptionPane.showConfirmDialog(frame,
                        "检测到已有文件，将覆盖生成。是否继续？\n" + String.join("\n", existingFiles),
                        "覆盖确认",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            saveConfig(current);
            generateForTables(current);
            JOptionPane.showMessageDialog(frame, "生成完成");
        });

        browsePathButton.addActionListener(e -> {
            if (browsingPath) {
                return;
            }
            browsingPath = true;
            try {
                String initial = pathField.getText().trim();
                JFileChooser chooser = new JFileChooser(initial);
                chooser.setDialogTitle("选择项目目录");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);
                int result = chooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION && chooser.getSelectedFile() != null) {
                    pathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            } finally {
                browsingPath = false;
            }
        });

        JPanel formPanel = new JPanel(new GridBagLayout());
        styleCard(formPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        addFormRow(formPanel, gbc, 0, "项目名", projectNameField);
        addPathRow(formPanel, gbc, 1, "项目目录", pathField, browsePathButton);
        addFormRow(formPanel, gbc, 2, "数据库IP", dbHostField);
        addFormRow(formPanel, gbc, 3, "端口", dbPortField);
        addFormRow(formPanel, gbc, 4, "数据库名", dbNameField);
        addFormRow(formPanel, gbc, 5, "用户名", usernameField);
        addFormRow(formPanel, gbc, 6, "密码", passwordField);
        addFormRow(formPanel, gbc, 7, "GroupId", groupIdField);

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.add(overwriteBox);
        checkboxPanel.add(disableOpenDirBox);
        checkboxPanel.add(multiSelectBox);
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 1;
        formPanel.add(checkboxPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        styleCard(buttonPanel);
        ((FlowLayout) buttonPanel.getLayout()).setHgap(14);
        ((FlowLayout) buttonPanel.getLayout()).setVgap(6);
        buttonPanel.add(testButton);
        buttonPanel.add(loadTableButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(generateButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(true);
        centerPanel.setBackground(BG_COLOR);
        JLabel headerLabel = new JLabel("数据库表（支持单选/多选）");
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        centerPanel.add(headerLabel, BorderLayout.NORTH);
        JScrollPane tableScroll = new JScrollPane(tableList);
        tableScroll.setBorder(null);
        tableScroll.getViewport().setBackground(LIST_BG);
        centerPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel templatePanel = new JPanel();
        templatePanel.setLayout(new BoxLayout(templatePanel, BoxLayout.Y_AXIS));
        styleCard(templatePanel);
        JLabel templateTitle = new JLabel("模板选择");
        templateTitle.setFont(templateTitle.getFont().deriveFont(Font.BOLD));
        templatePanel.add(templateTitle);
        templatePanel.add(controllerBox);
        templatePanel.add(queryBox);
        templatePanel.add(dtoBox);
        templatePanel.add(daoBox);
        templatePanel.add(doBox);
        templatePanel.add(apiserviceBox);
        templatePanel.add(apiServiceImplBox);
        templatePanel.add(serviceBox);
        templatePanel.add(serviceImplBox);
        templatePanel.add(mapperBox);
        templatePanel.add(managerBox);

        JScrollPane templateScroll = new JScrollPane(templatePanel);
        templateScroll.setBorder(null);
        templateScroll.getViewport().setBackground(CARD_COLOR);
        centerPanel.add(templateScroll, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(formPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void setGlobalUIFont() {
        Font font = new Font("Microsoft YaHei", Font.PLAIN, UI_FONT_SIZE);
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("List.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("OptionPane.buttonFont", font);
    }

    private static void styleCard(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));
    }

    private static void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.setOpaque(true);
    }

    private static void styleList(JList<String> list) {
        list.setBackground(LIST_BG);
        list.setSelectionBackground(ACCENT_DARK);
        list.setSelectionForeground(Color.WHITE);
        list.setBorder(null);
    }

    private static void styleCheckBox(JCheckBox box) {
        box.setOpaque(false);
        box.setBackground(CARD_COLOR);
        box.setForeground(new Color(45, 55, 72));
    }



    private static void restoreSelectedTables(JList<String> tableList, List<String> loadedTables, List<String> selectedTables) {
        if (selectedTables == null || selectedTables.isEmpty()) {
            return;
        }
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < loadedTables.size(); i++) {
            if (selectedTables.contains(loadedTables.get(i))) {
                indexes.add(i);
            }
        }
        int[] selectedIndexArray = new int[indexes.size()];
        for (int i = 0; i < indexes.size(); i++) {
            selectedIndexArray[i] = indexes.get(i);
        }
        tableList.setSelectedIndices(selectedIndexArray);
    }

    private static void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent input) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(input, gbc);
    }

    private static void addPathRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField input, JButton browseButton) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);

        JPanel pathPanel = new JPanel(new BorderLayout(8, 0));
        pathPanel.add(input, BorderLayout.CENTER);
        pathPanel.add(browseButton, BorderLayout.EAST);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(pathPanel, gbc);
    }

    private static String testConnection(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return "连接成功";
        } catch (Exception ex) {
            return "连接失败：" + ex.getMessage();
        }
    }

    private static List<String> queryTables(String url, String username, String password) {
        List<String> tables = new ArrayList<>();
        String sql = "SHOW TABLES";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "加载表失败：" + ex.getMessage());
        }
        Collections.sort(tables);
        return tables;
    }

    private static void generateForTables(GeneratorConfig config) {
        String url = config.getJdbcUrl();
        boolean disableOpenDir = Boolean.TRUE.equals(config.disableOpenDir);
        boolean genController = Boolean.TRUE.equals(config.genController);
        boolean genQuery = Boolean.TRUE.equals(config.genQuery);
        boolean genDto = Boolean.TRUE.equals(config.genDto);
        boolean genDao = Boolean.TRUE.equals(config.genDao);
        boolean genDo = Boolean.TRUE.equals(config.genDo);
        boolean genApiService = Boolean.TRUE.equals(config.genApiService);
        boolean genApiServiceImpl = Boolean.TRUE.equals(config.genApiServiceImpl);
        boolean genService = Boolean.TRUE.equals(config.genService);
        boolean genServiceImpl = Boolean.TRUE.equals(config.genServiceImpl);
        boolean genMapper = Boolean.TRUE.equals(config.genMapper);
        boolean genManager = Boolean.TRUE.equals(config.genManager);
        for (String tableName : config.selectedTables) {
            boolean fileOverride = config.overwrite;
            if (genController) {
                Controller.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genQuery) {
                Query.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genDto) {
                Dto.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genDao) {
                Dao.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genDo) {
                Do.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genApiService) {
                Apiservice.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genApiServiceImpl) {
                ApiServiceImpl.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genService) {
                Service.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genServiceImpl) {
                ServiceImpl.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genMapper) {
                Mapper.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
            if (genManager) {
                Manager.deal(config.projectName, tableName, config.path, url, config.username, config.password, config.groupId, disableOpenDir, fileOverride);
            }
        }
    }

    private static List<String> detectExistingFiles(GeneratorConfig config) {
        List<String> existed = new ArrayList<>();
        for (String table : config.selectedTables) {
            String entityName = toEntityName(table);
            if (Boolean.TRUE.equals(config.genQuery)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-client", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "client", "query"), entityName);
            }
            if (Boolean.TRUE.equals(config.genDto)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-client", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "client", "dto"), entityName);
            }
            if (Boolean.TRUE.equals(config.genApiService)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-client", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "client", "api"), entityName);
            }
            if (Boolean.TRUE.equals(config.genDo)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-core", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "core", "entity"), entityName);
            }
            if (Boolean.TRUE.equals(config.genDao)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-core", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "core", "dao"), entityName);
            }
            if (Boolean.TRUE.equals(config.genService)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-core", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "core", "service"), entityName);
            }
            if (Boolean.TRUE.equals(config.genManager)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-core", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "core", "manager"), entityName);
            }
            if (Boolean.TRUE.equals(config.genServiceImpl)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-core", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "core", "service", "impl"), entityName);
            }
            if (Boolean.TRUE.equals(config.genApiServiceImpl)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-core", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "core", "service", "api", "impl"), entityName);
            }
            if (Boolean.TRUE.equals(config.genController)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-web", "src", "main", "java", config.groupId.replace('.', File.separatorChar), "web", "controller"), entityName);
            }
            if (Boolean.TRUE.equals(config.genMapper)) {
                scanDirStartsWith(existed, Paths.get(config.path, config.projectName, config.projectName + "-core", "src", "main", "resources", "mapper"), entityName);
            }
        }
        return existed;
    }

    private static void scanDirStartsWith(List<String> existed, Path dir, String entityName) {
        File folder = dir.toFile();
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isFile() && file.getName().startsWith(entityName)) {
                existed.add(file.getAbsolutePath());
            }
        }
    }

    private static String toEntityName(String tableName) {
        String normalized = tableName;
        if (normalized.startsWith("t_")) {
            normalized = normalized.substring(2);
        } else if (normalized.startsWith("c_")) {
            normalized = normalized.substring(2);
        }
        String[] parts = normalized.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                sb.append(part.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private static GeneratorConfig buildConfig(JTextField projectNameField,
                                               JTextField pathField,
                                               JTextField dbHostField,
                                               JTextField dbPortField,
                                               JTextField dbNameField,
                                               JTextField usernameField,
                                               JPasswordField passwordField,
                                               JTextField groupIdField,
                                               JCheckBox overwriteBox,
                                               JCheckBox disableOpenDirBox,
                                               JCheckBox multiSelectBox,
                                               JCheckBox controllerBox,
                                               JCheckBox queryBox,
                                               JCheckBox dtoBox,
                                               JCheckBox daoBox,
                                               JCheckBox doBox,
                                               JCheckBox apiserviceBox,
                                               JCheckBox apiServiceImplBox,
                                               JCheckBox serviceBox,
                                               JCheckBox serviceImplBox,
                                               JCheckBox mapperBox,
                                               JCheckBox managerBox,
                                               JList<String> tableList) {
        GeneratorConfig config = new GeneratorConfig();
        config.projectName = projectNameField.getText().trim();
        config.path = pathField.getText().trim();
        config.dbHost = dbHostField.getText().trim();
        config.dbPort = dbPortField.getText().trim();
        config.dbName = dbNameField.getText().trim();
        config.username = usernameField.getText().trim();
        config.password = new String(passwordField.getPassword());
        config.groupId = groupIdField.getText().trim();
        config.overwrite = overwriteBox.isSelected();
        config.disableOpenDir = disableOpenDirBox.isSelected();
        config.multiSelect = multiSelectBox.isSelected();
        config.selectedTables = tableList.getSelectedValuesList();

        config.genController = controllerBox.isSelected();
        config.genQuery = queryBox.isSelected();
        config.genDto = dtoBox.isSelected();
        config.genDao = daoBox.isSelected();
        config.genDo = doBox.isSelected();
        config.genApiService = apiserviceBox.isSelected();
        config.genApiServiceImpl = apiServiceImplBox.isSelected();
        config.genService = serviceBox.isSelected();
        config.genServiceImpl = serviceImplBox.isSelected();
        config.genMapper = mapperBox.isSelected();
        config.genManager = managerBox.isSelected();
        return config;
    }

    private static GeneratorConfig loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            return GeneratorConfig.defaultConfig();
        }
        try {
            String json = new String(Files.readAllBytes(CONFIG_PATH), StandardCharsets.UTF_8);
            GeneratorConfig config = GSON.fromJson(json, GeneratorConfig.class);
            if (config == null) {
                return GeneratorConfig.defaultConfig();
            }
            config.fillDefaultIfMissing();
            return config;
        } catch (Exception ex) {
            return GeneratorConfig.defaultConfig();
        }
    }

    private static void saveConfig(GeneratorConfig config) {
        try {
            Files.write(CONFIG_PATH, GSON.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "保存配置失败：" + ex.getMessage());
        }
    }

    private static class GeneratorConfig {
        private static final String DEFAULT_PROJECT_NAME = "tech-muyi-base";
        private static final String DEFAULT_DB_NAME = "tech_muyi_base";
        private static final String DEFAULT_GROUP_ID = "tech.muyi";

        private String projectName;
        private String path;
        private String dbHost;
        private String dbPort;
        private String dbName;
        private String url;
        private String username;
        private String password;
        private String groupId;
        private boolean overwrite;
        private Boolean disableOpenDir;
        private boolean multiSelect;
        private Boolean genController;
        private Boolean genQuery;
        private Boolean genDto;
        private Boolean genDao;
        private Boolean genDo;
        private Boolean genApiService;
        private Boolean genApiServiceImpl;
        private Boolean genService;
        private Boolean genServiceImpl;
        private Boolean genMapper;
        private Boolean genManager;
        private List<String> selectedTables;

        private static GeneratorConfig defaultConfig() {
            GeneratorConfig config = new GeneratorConfig();
            config.projectName = DEFAULT_PROJECT_NAME;
            config.path = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize().toString();
            config.dbHost = "127.0.0.1";
            config.dbPort = "3306";
            config.dbName = DEFAULT_DB_NAME;
            config.url = buildJdbcUrl(config.dbHost, config.dbPort, config.dbName);
            config.username = "root";
            config.password = "";
            config.groupId = DEFAULT_GROUP_ID;
            config.overwrite = false;
            config.disableOpenDir = true;
            config.multiSelect = true;
            config.genController = true;
            config.genQuery = true;
            config.genDto = true;
            config.genDao = true;
            config.genDo = true;
            config.genApiService = true;
            config.genApiServiceImpl = true;
            config.genService = true;
            config.genServiceImpl = true;
            config.genMapper = true;
            config.genManager = true;
            config.selectedTables = new ArrayList<>();
            return config;
        }

        private void fillDefaultIfMissing() {
            GeneratorConfig defaults = defaultConfig();
            if (projectName == null || projectName.trim().isEmpty()) {
                projectName = defaults.projectName;
            }
            if (path == null || path.trim().isEmpty()) {
                path = defaults.path;
            }
            if ((dbHost == null || dbHost.trim().isEmpty())
                    || (dbPort == null || dbPort.trim().isEmpty())
                    || (dbName == null || dbName.trim().isEmpty())) {
                fillDbPartsFromUrl(url);
            }
            if (dbHost == null || dbHost.trim().isEmpty()) {
                dbHost = defaults.dbHost;
            }
            if (dbPort == null || dbPort.trim().isEmpty()) {
                dbPort = defaults.dbPort;
            }
            if (dbName == null || dbName.trim().isEmpty()) {
                dbName = defaults.dbName;
            }
            if (username == null || username.trim().isEmpty()) {
                username = defaults.username;
            }
            if (password == null) {
                password = defaults.password;
            }
            if (groupId == null || groupId.trim().isEmpty()) {
                groupId = defaults.groupId;
            }
            if (selectedTables == null) {
                selectedTables = new ArrayList<>();
            }
            if (disableOpenDir == null) disableOpenDir = defaults.disableOpenDir;
            if (genController == null) genController = defaults.genController;
            if (genQuery == null) genQuery = defaults.genQuery;
            if (genDto == null) genDto = defaults.genDto;
            if (genDao == null) genDao = defaults.genDao;
            if (genDo == null) genDo = defaults.genDo;
            if (genApiService == null) genApiService = defaults.genApiService;
            if (genApiServiceImpl == null) genApiServiceImpl = defaults.genApiServiceImpl;
            if (genService == null) genService = defaults.genService;
            if (genServiceImpl == null) genServiceImpl = defaults.genServiceImpl;
            if (genMapper == null) genMapper = defaults.genMapper;
            if (genManager == null) genManager = defaults.genManager;
            url = getJdbcUrl();
        }

        private String getJdbcUrl() {
            return buildJdbcUrl(dbHost, dbPort, dbName);
        }

        private void fillDbPartsFromUrl(String jdbcUrl) {
            if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
                return;
            }
            String urlValue = jdbcUrl.trim();
            String withoutPrefix = urlValue;
            if (withoutPrefix.startsWith("jdbc:mysql://")) {
                withoutPrefix = withoutPrefix.substring("jdbc:mysql://".length());
            }

            int slashIndex = withoutPrefix.indexOf('/');
            if (slashIndex <= 0) {
                return;
            }

            String hostPort = withoutPrefix.substring(0, slashIndex);
            String dbAndParams = withoutPrefix.substring(slashIndex + 1);

            int colonIndex = hostPort.lastIndexOf(':');
            if (colonIndex > 0 && colonIndex < hostPort.length() - 1) {
                if (dbHost == null || dbHost.trim().isEmpty()) {
                    dbHost = hostPort.substring(0, colonIndex);
                }
                if (dbPort == null || dbPort.trim().isEmpty()) {
                    dbPort = hostPort.substring(colonIndex + 1);
                }
            } else {
                if (dbHost == null || dbHost.trim().isEmpty()) {
                    dbHost = hostPort;
                }
            }

            int questionMarkIndex = dbAndParams.indexOf('?');
            String parsedDbName = questionMarkIndex >= 0 ? dbAndParams.substring(0, questionMarkIndex) : dbAndParams;
            if (dbName == null || dbName.trim().isEmpty()) {
                dbName = parsedDbName;
            }
        }
    }

    private static String buildJdbcUrl(String dbHost, String dbPort, String dbName) {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF8&serverTimezone=GMT%2B8";
    }
}
