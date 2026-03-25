package com.joshiminh.wgcinema.ui.dashboard;
import com.joshiminh.wgcinema.util.*;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.joshiminh.wgcinema.dao.*;
import com.joshiminh.wgcinema.util.BaseEditorDialog;
import com.joshiminh.wgcinema.util.ResourceManager;

import static com.joshiminh.wgcinema.util.AgentStyles.*;

@SuppressWarnings("unused")
public class MovieEditorDialog extends BaseEditorDialog {
  private String[] movieColumns;
  private final boolean isNewMovie;
  private final int movieId;
  private final List<JComponent> inputComponents;

  public MovieEditorDialog(String url, int id, boolean newMovie, Runnable refreshCallback) {
      super(url, newMovie ? "Add New Movie" : "Edit Movie", 800, 825, refreshCallback);
      movieId = id;
      isNewMovie = newMovie;
      inputComponents = new ArrayList<>();

      setupFrame();
  }

  @Override
  protected void setupFrame() {
      loadMovieData();
      add(createHeader(isNewMovie ? "Add New Movie" : "Edit Movie"), BorderLayout.NORTH);
      add(createFormPanel(), BorderLayout.CENTER);
      add(createFooter(isNewMovie ? "Add Movie" : "Save Changes", this::performSaveAction), BorderLayout.SOUTH);
  }

  private void loadMovieData() {
      movieColumns = BaseDAO.getColumnNames(databaseUrl, "movies");
  }

  private JPanel createFormPanel() {
      JPanel formContainer = new JPanel(new GridBagLayout());
      formContainer.setBackground(PRIMARY_BACKGROUND);
      formContainer.setBorder(BorderFactory.createEmptyBorder(10, FORM_PADDING, 10, FORM_PADDING));

      JPanel formPanel = new JPanel(new GridBagLayout());
      formPanel.setBackground(PRIMARY_BACKGROUND);

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = LABEL_INSETS;
      gbc.anchor = GridBagConstraints.WEST;

      String[] columnValues = loadColumnValues();

      for (int i = 0; i < movieColumns.length; i++) {
          JLabel label = createFormLabel(movieColumns[i]);
          gbc.gridx = 0;
          gbc.gridy = i;
          gbc.weightx = 0.25;
          gbc.fill = GridBagConstraints.NONE;
          formPanel.add(label, gbc);

          JComponent inputComponent = createInputComponent(movieColumns[i], columnValues[i]);
          inputComponents.add(inputComponent);
          gbc.gridx = 1;
          gbc.weightx = 0.75;
          gbc.fill = GridBagConstraints.HORIZONTAL;
          formPanel.add(inputComponent, gbc);
      }

      GridBagConstraints formGbc = new GridBagConstraints();
      formGbc.fill = GridBagConstraints.BOTH;
      formGbc.weightx = 1;
      formGbc.weighty = 1;
      formContainer.add(formPanel, formGbc);

      return formContainer;
  }

  private String[] loadColumnValues() {
      String[] columnValues = new String[movieColumns.length];
      if (isNewMovie) {
          for (int i = 0; i < movieColumns.length; i++) {
              columnValues[i] = "";
          }
      } else {
          try (ResultSet resultSet = MovieDAO.fetchMovieDetails(databaseUrl, movieId)) {
              if (resultSet.next()) {
                  for (int i = 0; i < movieColumns.length; i++) {
                      columnValues[i] = resultSet.getString(movieColumns[i]);
                  }
              }
          } catch (SQLException e) {
              showError("Error loading movie details: " + e.getMessage());
          }
      }
      return columnValues;
  }

  private JComponent createInputComponent(String fieldName, String defaultValue) {
      return switch (fieldName.toLowerCase()) {
          case "age_rating" -> createRatingComboBox(defaultValue);
          case "release_date" -> createDateSpinner(defaultValue);
          case "description" -> createDescriptionTextArea(defaultValue);
          default -> createTextField(fieldName, defaultValue);
      };
  }

  private JComboBox<String> createRatingComboBox(String defaultValue) {
      JComboBox<String> comboBox = new JComboBox<>(AgeRatingColor.getRatings());
      comboBox.setSelectedItem(defaultValue);
      styleComponent(comboBox);
      comboBox.setPreferredSize(new Dimension(0, 30));
      return comboBox;
  }

  private JSpinner createDateSpinner(String defaultValue) {
      JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
      JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
      dateSpinner.setEditor(dateEditor);
      styleComponent(dateSpinner);
      try {
          if (!defaultValue.isEmpty()) {
              dateSpinner.setValue(java.sql.Date.valueOf(defaultValue));
          }
      } catch (IllegalArgumentException ignored) {}
      dateSpinner.setPreferredSize(new Dimension(0, 30));
      return dateSpinner;
  }

  private JScrollPane createDescriptionTextArea(String defaultValue) {
      JTextArea textArea = new JTextArea(defaultValue, 4, 20);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      styleComponent(textArea);
      textArea.setCaretColor(TEXT_COLOR);

      JScrollPane scrollPane = new JScrollPane(textArea);
      scrollPane.setPreferredSize(new Dimension(0, 150));
      return scrollPane;
  }

  private JTextField createTextField(String fieldName, String defaultValue) {
      JTextField textField = new JTextField(defaultValue);
      styleComponent(textField);
      textField.setBorder(componentBorder());
      textField.setCaretColor(TEXT_COLOR);
      textField.setEditable(!fieldName.equalsIgnoreCase("id"));

      if (fieldName.equalsIgnoreCase("id") && isNewMovie) {
          textField.setText("Auto-generated by DB");
          textField.setForeground(LIGHT_TEXT_COLOR);
      }
      textField.setPreferredSize(new Dimension(0, 30));
      return textField;
  }

  private void performSaveAction() {
      if (isNewMovie) {
          addNewMovie();
      } else {
          saveChanges();
      }
  }

  private void addNewMovie() {
      String[] allValues = extractComponentValues(inputComponents);

      List<String> filteredColumns = new ArrayList<>();
      List<String> filteredValues = new ArrayList<>();

      for (int i = 0; i < movieColumns.length; i++) {
          if (!movieColumns[i].equalsIgnoreCase("id")) {
              filteredColumns.add(movieColumns[i]);
              filteredValues.add(allValues[i]);
          }
      }

      int result = MovieDAO.insertMovie(databaseUrl, filteredColumns.toArray(new String[0]), filteredValues.toArray(new String[0]));

      showResult(result > 0 ? "Movie added successfully!" : "Failed to add movie", result > 0);
      if (result > 0 && refreshCallback != null) {
          refreshCallback.run();
      }
      dispose();
  }

  private void saveChanges() {
      String[] updatedValues = extractComponentValues(inputComponents);
      int result = MovieDAO.updateMovieById(databaseUrl, movieColumns, updatedValues, movieId);
      showResult(result > 0 ? "Changes saved successfully!" : "No changes were made", result > 0);
      if (result > 0 && refreshCallback != null) {
          refreshCallback.run();
      }
      dispose();
  }
}
