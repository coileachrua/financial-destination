package com.savingsplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.savingsplanner.model.persistence.SaveFile;
import lombok.extern.log4j.Log4j2;

import java.util.prefs.Preferences;

import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

@Log4j2
public class PersistenceService implements Serializable {
    @Serial
    private static final long serialVersionUID = 646463464646L;

    private final Preferences prefs = Preferences.userNodeForPackage(PersistenceService.class);
    private File file;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    public PersistenceService() {
        this.file = new File(prefs.get("dataFilePath", "savings_data.json"));
    }

    public void setFilePath(String path) {
        this.file = new File(path);
        prefs.put("dataFilePath", path);
    }

    public void load(SavingsPlanner planner) {
        if (!file.exists()) return;
        try {
            SaveFile sf = mapper.readValue(file, SaveFile.class);
            planner.clearAll();
            sf.users().forEach(planner::addUser);
            sf.expenses().forEach(planner::addExpense);
            sf.goals().forEach(planner::setGoal);
        } catch (IOException e) {
            log.error("Failed to load data", e);
        }
    }

    public void save(SavingsPlanner planner) {
        try {
            SaveFile sf = new SaveFile(
                    planner.getUsers(),
                    planner.getExpenses(),
                    planner.getGoals()
            );
            mapper.writeValue(file, sf);
        } catch (IOException e) {
            log.error("Failed to save data", e);
        }
    }
}
