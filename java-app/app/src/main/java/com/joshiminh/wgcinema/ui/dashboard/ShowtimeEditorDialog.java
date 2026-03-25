package com.joshiminh.wgcinema.ui.dashboard;
import com.joshiminh.wgcinema.util.*;

import com.joshiminh.wgcinema.dao.ShowtimeDAO;
import com.joshiminh.wgcinema.dao.BaseDAO;
import com.joshiminh.wgcinema.util.BaseEditorDialog;
import com.joshiminh.wgcinema.util.ResourceManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.joshiminh.wgcinema.util.AgentStyles.*;

@SuppressWarnings("unused")
public class ShowtimeEditorDialog extends BaseEditorDialog {
  private String[] showtimeColumns;
  private final List<JComponent> inputComponents;

  public ShowtimeEditorDialog(String url, Runnable refreshCallback) {
      super(url, "Add New Showtime", 700, 700, refreshCallback);
      inputComponents = new ArrayList<>();
      setupFrame();
  }

  @Override
  protected void setupFrame() {
      add(createHeader("Add New Showtime"), BorderLayout.NORTH);
      add(createFormPanel(), BorderLayout.CENTER);
      add(createFooter("Add Showtime", this::performSaveAction), BorderLayout.SOUTH);
  }

  private JPanel createFormPanel() {
      showtimeColumns = getFilteredColumns();
      JPanel container = new JPanel(new GridBagLayout());
      container.setBackground(PRIMARY_BACKGROUND);
      container.setBorder(new EmptyBorder(10, FORM_PADDING, 10, FORM_PADDING));

      JPanel form = new JPanel(new GridBagLayout());
      form.setBackground(PRIMARY_BACKGROUND);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = LABEL_INSETS;
      gbc.anchor = GridBagConstraints.WEST;

      for (int i = 0; i < showtimeColumns.length; i++) {
          JLabel label = createFormLabel(showtimeColumns[i]);
          gbc.gridx = 0;
          gbc.gridy = i;
          gbc.weightx = 0.25;
          gbc.fill = GridBagConstraints.NONE;
          form.add(label, gbc);

          JComponent input = createInputComponent(showtimeColumns[i]);
          inputComponents.add(input);
          gbc.gridx = 1;
          gbc.gridy = i;
          gbc.weightx = 0.75;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          form.add(input, gbc);
      }

      GridBagConstraints formGbc = new GridBagConstraints();
      formGbc.fill = GridBagConstraints.BOTH;
      formGbc.weightx = 1;
      formGbc.weighty = 1;
      container.add(form, formGbc);
      return container;
  }

  private JComponent createInputComponent(String fieldName) {
      JTextField field = new JTextField();
      styleComponent(field);
      field.setBorder(componentBorder());
      field.setCaretColor(TEXT_COLOR);
      field.setPreferredSize(new Dimension(0, 30));
      return field;
  }

  private String[] getFilteredColumns() {
      List<String> columns = new ArrayList<>();
      for (String col : BaseDAO.getColumnNames(databaseUrl, "showtimes")) {
          if (!col.equalsIgnoreCase("showtime_id")) {
              columns.add(col);
          }
      }
      return columns.toArray(new String[0]);
  }

  private void performSaveAction() {
      String[] values = extractComponentValues(inputComponents);
      int result = ShowtimeDAO.insertShowtime(databaseUrl, showtimeColumns, values);

      if (result > 0) {
          showResult("Showtime added successfully!", true);
          if (refreshCallback != null) {
              refreshCallback.run();
          }
          dispose();
      } else {
          showResult("Failed to add showtime", false);
      }
  }
}
