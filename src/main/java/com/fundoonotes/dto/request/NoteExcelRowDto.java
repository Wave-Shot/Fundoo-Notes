package com.fundoonotes.dto.request;

/*
 * Represents one row read from the Excel file.
 * Each field maps to one column in the spreadsheet.
 * We keep it simple — title, description, and owner email.
 */
public class NoteExcelRowDto {

    private String title;
    private String description;
    private String ownerEmail;
    private String color;

    public NoteExcelRowDto() {}

    public NoteExcelRowDto(String title, String description,
                           String ownerEmail, String color) {
        this.title = title;
        this.description = description;
        this.ownerEmail = ownerEmail;
        this.color = color;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() {
        return "NoteExcelRowDto{title='" + title + "', ownerEmail='" + ownerEmail + "'}";
    }
}