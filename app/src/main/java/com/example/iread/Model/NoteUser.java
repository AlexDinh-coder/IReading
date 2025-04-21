package com.example.iread.Model;

public class NoteUser {
    private int id;
    private String userName;
    private String userId;
    private String chapterId;
    private int start;
    private int end;
    private String selectedText;
    private String noteContent;
    private String color;
    private String createDate;
    private int bookId;

    public NoteUser() {
    }

//    public NoteUser(int id, String userName, String userId, String chapterId, int start, int end, String selectedText, String noteContent, String color, Date createDate) {
//        this.id = id;
//        this.userName = userName;
//        this.userId = userId;
//        this.chapterId = chapterId;
//        this.start = start;
//        this.end = end;
//        this.selectedText = selectedText;
//        this.noteContent = noteContent;
//        this.color = color;
//        this.createDate = createDate;
//    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() {return bookId;
    }

    public void setBookId(int bookId) {this.bookId = bookId;
    }


    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }

    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }

    public int getEnd() { return end; }
    public void setEnd(int end) { this.end = end; }

    public String getSelectedText() { return selectedText; }
    public void setSelectedText(String selectedText) { this.selectedText = selectedText; }

    public String getNoteContent() { return noteContent; }
    public void setNoteContent(String noteContent) { this.noteContent = noteContent; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getCreateDate() { return createDate; }
    public void setCreateDate(String createDate) { this.createDate = createDate; }
}
