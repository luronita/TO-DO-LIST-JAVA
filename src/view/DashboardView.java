package view;

import controller.TaskController;
import model.Task;
import model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardView extends JFrame {

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private String currentFilter = "All";

    private TaskController taskController;
    private User currentUser;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG          = new Color(0xF4F6FB);
    private static final Color SURFACE     = Color.WHITE;
    private static final Color BORDER_CLR  = new Color(0xE4E8F0);
    private static final Color PRIMARY     = new Color(0x2563EB);
    private static final Color PRIMARY_HOV = new Color(0x1D4ED8);
    private static final Color SUCCESS     = new Color(0x16A34A);
    private static final Color DANGER      = new Color(0xDC2626);
    private static final Color MUTED       = new Color(0x6B7280);
    private static final Color TEXT_MAIN   = new Color(0x111827);
    private static final Color TEXT_SEC    = new Color(0x6B7280);
    private static final Color ACCENT      = new Color(0xEFF6FF);
    private static final Color HEADER_BG   = new Color(0x1E3A8A);
    private static final Color ROW_EVEN    = Color.WHITE;
    private static final Color ROW_ODD     = new Color(0xF9FAFB);
    private static final Color ROW_SEL     = new Color(0xDBEAFE);

    // ── Stat counters ─────────────────────────────────────────────────────────
    private JLabel totalCountLabel;
    private JLabel doneCountLabel;
    private JLabel todoCountLabel;

    // ── Filter tab buttons ────────────────────────────────────────────────────
    private JButton tabAll, tabTodo, tabDone;

    public DashboardView(User user) {
        this.currentUser = user;
        this.taskController = new TaskController();

        setTitle("Tasks — " + user.getName());
        setSize(960, 620);
        setMinimumSize(new Dimension(760, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        loadTasks("All");
        setVisible(true);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(SURFACE);
        outer.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(16, 24, 16, 24)
        ));

        // Left: avatar + greeting
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JPanel avatar = buildAvatar(user_initials(currentUser.getName()));
        left.add(avatar);

        JPanel greetBlock = new JPanel();
        greetBlock.setLayout(new BoxLayout(greetBlock, BoxLayout.Y_AXIS));
        greetBlock.setOpaque(false);
        JLabel name = new JLabel("Welcome, " + currentUser.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 18));
        name.setForeground(TEXT_MAIN);
        JLabel subtitle = new JLabel("Gérez vos tâches du jour");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_SEC);
        greetBlock.add(name);
        greetBlock.add(subtitle);
        left.add(greetBlock);

        // Right: stats + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        totalCountLabel = buildStatChip("Total", "0", new Color(0xE0E7FF), new Color(0x3730A3));
        doneCountLabel  = buildStatChip("Terminées", "0", new Color(0xDCFCE7), new Color(0x15803D));
        todoCountLabel  = buildStatChip("À faire", "0", new Color(0xFEF9C3), new Color(0x92400E));

        right.add(totalCountLabel.getParent()); // the chip panel
        right.add(doneCountLabel.getParent());
        right.add(todoCountLabel.getParent());
        right.add(Box.createHorizontalStrut(8));
        right.add(buildIconButton("Déconnexion", MUTED, e -> { dispose(); new LoginView(); }));

        outer.add(left, BorderLayout.WEST);
        outer.add(right, BorderLayout.EAST);
        return outer;
    }

    private JPanel buildAvatar(String initials) {
        JPanel p = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(initials, x, y);
            }
        };
        p.setPreferredSize(new Dimension(42, 42));
        p.setOpaque(false);
        return p;
    }

    private JLabel buildStatChip(String label, String value, Color bg, Color fg) {
        JPanel chip = new JPanel(new BorderLayout(4, 0));
        chip.setBackground(bg);
        chip.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 0),
                new EmptyBorder(5, 12, 5, 12)
        ));
        chip.setOpaque(true);
        // rounded via override
        JPanel wrapper = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        wrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
        wrapper.setOpaque(false);

        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(fg);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(fg);

        wrapper.add(lbl);
        wrapper.add(val);

        // Return the value label so we can update it later
        // But we also need the wrapper in the header — store as client property
        val.putClientProperty("wrapper", wrapper);
        return val;
    }

    // ── CENTER ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 24, 0, 24));

        // Toolbar: filter tabs + Add button
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel tabs = buildFilterTabs();
        JButton addBtn = buildPrimaryButton("+ Nouvelle tâche", PRIMARY);
        addBtn.addActionListener(e -> new TaskFormView(currentUser, null, DashboardView.this));

        toolbar.add(tabs, BorderLayout.WEST);
        toolbar.add(addBtn, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Titre", "Description", "Statut", "Échéance"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        taskTable = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ROW_EVEN : ROW_ODD);
                }
                return c;
            }
        };

        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taskTable.setRowHeight(36);
        taskTable.setShowVerticalLines(false);
        taskTable.setGridColor(BORDER_CLR);
        taskTable.setSelectionBackground(ROW_SEL);
        taskTable.setSelectionForeground(TEXT_MAIN);
        taskTable.setIntercellSpacing(new Dimension(0, 1));
        taskTable.setFocusable(false);

        // Header
        JTableHeader header = taskTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Hide ID column
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Column widths
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(280);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        // Status renderer
        taskTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());

        // Cell padding renderer
        DefaultTableCellRenderer padRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        };
        taskTable.getColumnModel().getColumn(1).setCellRenderer(padRenderer);
        taskTable.getColumnModel().getColumn(2).setCellRenderer(padRenderer);
        taskTable.getColumnModel().getColumn(4).setCellRenderer(padRenderer);

        JScrollPane scroll = new JScrollPane(taskTable);
        scroll.setBorder(new LineBorder(BORDER_CLR, 1));
        scroll.getViewport().setBackground(SURFACE);
        scroll.setBackground(SURFACE);

        // rounded card wrapper
        JPanel card = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_CLR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.add(scroll);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildFilterTabs() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);

        tabAll  = buildTabButton("Toutes",     true);
        tabTodo = buildTabButton("À faire",    false);
        tabDone = buildTabButton("Terminées",  false);

        tabAll.addActionListener(e  -> applyFilter("All",  tabAll));
        tabTodo.addActionListener(e -> applyFilter("TODO", tabTodo));
        tabDone.addActionListener(e -> applyFilter("DONE", tabDone));

        // group in a pill container
        JPanel group = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xE5E7EB));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
        };
        group.setOpaque(false);
        group.setBorder(new EmptyBorder(4, 4, 4, 4));
        group.add(tabAll);
        group.add(tabTodo);
        group.add(tabDone);
        p.add(group);
        return p;
    }

    private JButton buildTabButton(String text, boolean active) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getClientProperty("active") != null && (Boolean) getClientProperty("active")) {
                    g2.setColor(SURFACE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("active", active);
        styleTabBtn(btn, active);
        return btn;
    }

    private void styleTabBtn(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
        btn.setForeground(active ? TEXT_MAIN : TEXT_SEC);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
    }

    private void applyFilter(String filter, JButton activeTab) {
        currentFilter = filter;
        for (JButton tb : new JButton[]{tabAll, tabTodo, tabDone}) {
            tb.putClientProperty("active", tb == activeTab);
            styleTabBtn(tb, tb == activeTab);
        }
        loadTasks(filter);
        repaint();
    }

    // ── FOOTER ────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 14));
        panel.setBackground(SURFACE);
        panel.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton editBtn   = buildOutlineButton("✏  Modifier",   PRIMARY);
        JButton delBtn    = buildOutlineButton("🗑  Supprimer",  DANGER);
        JButton doneBtn   = buildPrimaryButton("✔  Marquer Fait", SUCCESS);

        editBtn.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row == -1) { showWarn("Veuillez sélectionner une tâche."); return; }
            new TaskFormView(currentUser, getTaskFromRow(row), DashboardView.this);
        });

        delBtn.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row == -1) { showWarn("Veuillez sélectionner une tâche."); return; }
            int ok = JOptionPane.showConfirmDialog(this,
                    "Supprimer cette tâche ?", "Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) {
                taskController.deleteTask((int) tableModel.getValueAt(row, 0));
                loadTasks(currentFilter);
            }
        });

        doneBtn.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row == -1) { showWarn("Veuillez sélectionner une tâche."); return; }
            Task t = getTaskFromRow(row);
            t.markAsCompleted();
            taskController.updateTask(t);
            loadTasks(currentFilter);
            JOptionPane.showMessageDialog(this, "Tâche marquée comme terminée !");
        });

        panel.add(editBtn);
        panel.add(delBtn);
        panel.add(doneBtn);
        return panel;
    }

    // ── BUTTON HELPERS ────────────────────────────────────────────────────────
    private JButton buildPrimaryButton(String text, Color color) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildOutlineButton(String text, Color color) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 18));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(color);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildIconButton(String text, Color color, ActionListener al) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(color);
        btn.setBackground(new Color(0,0,0,0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 10, 6, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(al);
        return btn;
    }

    // ── STATUS BADGE RENDERER ─────────────────────────────────────────────────
    static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean sel, boolean foc, int row, int col) {
            JLabel lbl = new JLabel(value != null ? value.toString() : "") {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 20, 20);
                    super.paintComponent(g);
                }
            };
            String status = value != null ? value.toString().toUpperCase() : "";
            if (status.equals("DONE")) {
                lbl.setBackground(new Color(0xDCFCE7));
                lbl.setForeground(new Color(0x15803D));
            } else {
                lbl.setBackground(new Color(0xFEF9C3));
                lbl.setForeground(new Color(0x92400E));
            }
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setHorizontalAlignment(JLabel.CENTER);
            lbl.setOpaque(false);
            if (sel) lbl.setOpaque(false);
            lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
            return lbl;
        }
    }

    // ── DATA ──────────────────────────────────────────────────────────────────
    public void loadTasks(String filter) {
        tableModel.setRowCount(0);
        List<Task> tasks = taskController.getTasksByUser(currentUser.getId());
        if (tasks == null) return;

        int total = 0, done = 0, todo = 0;
        for (Task task : tasks) {
            total++;
            String status = task.getStatus();
            if (status.equalsIgnoreCase("DONE")) done++;
            else todo++;

            if (filter.equals("DONE") && !status.equalsIgnoreCase("DONE")) continue;
            if (filter.equals("TODO") &&  status.equalsIgnoreCase("DONE")) continue;

            String due = task.getDueDate() != null ? task.getDueDate().format(formatter) : "";
            tableModel.addRow(new Object[]{task.getId(), task.getTitle(),
                    task.getDescription(), status, due});
        }
        updateStats(total, done, todo);
    }

    private void updateStats(int total, int done, int todo) {
        if (totalCountLabel != null) totalCountLabel.setText(String.valueOf(total));
        if (doneCountLabel  != null) doneCountLabel.setText(String.valueOf(done));
        if (todoCountLabel  != null) todoCountLabel.setText(String.valueOf(todo));
    }

    private Task getTaskFromRow(int row) {
        Task task = new Task();
        task.setId((int) tableModel.getValueAt(row, 0));
        task.setTitle((String) tableModel.getValueAt(row, 1));
        task.setDescription((String) tableModel.getValueAt(row, 2));
        task.setStatus((String) tableModel.getValueAt(row, 3));
        task.setUser(currentUser);
        String due = (String) tableModel.getValueAt(row, 4);
        task.setDueDate((due != null && !due.isEmpty())
                ? LocalDateTime.parse(due, formatter)
                : LocalDateTime.now().plusDays(1));
        return task;
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    private String user_initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }
}