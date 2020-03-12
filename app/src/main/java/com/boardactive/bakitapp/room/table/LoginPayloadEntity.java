package com.boardactive.bakitapp.room.table;

//@Entity(tableName = "loginPayload")
//public class LoginPayloadEntity {

//    @Embedded
//    public AppEntity[] apps;
//
//    @ColumnInfo(name = "isClaimed")
//    public Boolean isClaimed;
//
//    @PrimaryKey
//    public Long id;
//
//    @ColumnInfo(name = "email")
//    public String email;
//
//    @ColumnInfo(name = "guid")
//    public String guid;
//
//    @ColumnInfo(name = "firstName")
//    public String firstName;
//
//    @ColumnInfo(name = "lastName")
//    public String lastName;
//
//    @ColumnInfo(name = "avatarURL")
//    public String avatarURL;
//
//    @ColumnInfo(name = "avatarImageID")
//    public String avatarImageID;
//
//    @ColumnInfo(name = "googleAvatarURL")
//    public String googleAvatarURL;
//
//    @ColumnInfo(name = "customerID")
//    public String customerID;
//
//    @ColumnInfo(name = "isCompliant")
//    public Boolean isCompliant;
//
//    @ColumnInfo(name = "isGod")
//    public Boolean isGod;
//
//    @ColumnInfo(name = "isApprovedByDoug")
//    public Boolean isApprovedByDoug;
//
//    @ColumnInfo(name = "isRejectedByDoug")
//    public Boolean isRejectedByDoug;
//
//    @ColumnInfo(name = "isVerified")
//    public Boolean isVerified;
//
//    @ColumnInfo(name = "dateCreated")
//    public String dateCreated;
//
//    @ColumnInfo(name = "dateLastUpdated")
//    public String dateLastUpdated;
//
//    @ColumnInfo(name = "dateDeleted")
//    public String dateDeleted;
//
//    @ColumnInfo(name = "inbox")
//    public String inbox;
//
//    public AppEntity[] getApps() {
//        return apps;
//    }
//    public void setApps(AppEntity[] apps) {
//        this.apps = apps;
//    }
//
//    public Boolean getIsClaimed() {
//        return isClaimed;
//    }
//    public void setIsClaimed(Boolean isClaimed) {
//        this.isClaimed = isClaimed;
//    }
//
//    public Long getId() {
//        return id;
//    }
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getGuid() {
//        return guid;
//    }
//    public void setGuid(String guid) {
//        this.guid = guid;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getAvatarURL() {
//        return avatarURL;
//    }
//    public void setAvatarURL(String avatarURL) {
//        this.avatarURL = avatarURL;
//    }
//
//    public String getAvatarImageID() {
//        return avatarImageID;
//    }
//    public void setAvatarImageID(String avatarImageID) {
//        this.avatarImageID = avatarImageID;
//    }
//
//    public String getGoogleAvatarURL() {
//        return googleAvatarURL;
//    }
//    public void setGoogleAvatarURL(String googleAvatarURL) {
//        this.googleAvatarURL = googleAvatarURL;
//    }
//
//    public String getCustomerID() {
//        return customerID;
//    }
//    public void setCustomerID(String customerID) {
//        this.customerID = customerID;
//    }
//
//    public Boolean getIsCompliant() {
//        return isCompliant;
//    }
//    public void setIsCompliant(Boolean isCompliant) {
//        this.isCompliant = isCompliant;
//    }
//
//    public Boolean getIsGod() {
//        return isGod;
//    }
//    public void setIsGod(Boolean isGod) {
//        this.isGod = isGod;
//    }
//
//    public Boolean getIsApprovedByDoug() {
//        return isApprovedByDoug;
//    }
//    public void setIsApprovedByDoug(Boolean isApprovedByDoug) {
//        this.isApprovedByDoug = isApprovedByDoug;
//    }
//
//    public Boolean getIsRejectedByDoug() {
//        return isRejectedByDoug;
//    }
//    public void setIsRejectedByDoug(Boolean isRejectedByDoug) {
//        this.isRejectedByDoug = isRejectedByDoug;
//    }
//
//    public Boolean getIsVerified() {
//        return isVerified;
//    }
//    public void setIsVerified(Boolean isVerified) {
//        this.isVerified = isVerified;
//    }
//
//    public String getDateCreated() {
//        return dateCreated;
//    }
//    public void setDateCreated(String dateCreated) {
//        this.dateCreated = dateCreated;
//    }
//
//    public String getDateLastUpdated() {
//        return dateLastUpdated;
//    }
//    public void setDateLastUpdated(String dateLastUpdated) {
//        this.dateLastUpdated = dateLastUpdated;
//    }
//
//    public String getDateDeleted() {
//        return dateDeleted;
//    }
//    public void setDateDeleted(String dateDeleted) {
//        this.dateDeleted = dateDeleted;
//    }
//
//    public String getInbox() {
//        return inbox;
//    }
//    public void setInbox(String inbox) {
//        this.inbox = inbox;
//    }
//
//    public static LoginPayloadEntity entity(LoginPayload loginPayload) {
//        LoginPayloadEntity entity = new LoginPayloadEntity();
//        entity.setApps(loginPayload.getApps());
//        entity.setIsClaimed(loginPayload.getIsClaimed());
//        entity.setId(loginPayload.getID());
//        entity.setGuid(loginPayload.getGUID());
//        entity.setEmail(loginPayload.getEmail());
//        entity.setFirstName(loginPayload.getFirstName());
//        entity.setLastName(loginPayload.getLastName());
//        entity.setAvatarURL(loginPayload.getAvatarURL());
//        entity.setAvatarImageID(loginPayload.getAvatarImageID());
//        entity.setGoogleAvatarURL(loginPayload.getGoogleAvatarURL());
//        entity.setCustomerID(loginPayload.getCustomerID());
//        entity.setIsCompliant(loginPayload.getIsCompliant());
//        entity.setIsGod(loginPayload.getIsGod());
//        entity.setIsApprovedByDoug(loginPayload.getIsApprovedByDoug());
//        entity.setIsRejectedByDoug(loginPayload.getIsRejectedByDoug());
//        entity.setIsVerified(loginPayload.getIsVerified());
//        entity.setDateCreated(loginPayload.getDateCreated());
//        entity.setDateLastUpdated(loginPayload.getDateLastUpdated());
//        entity.setDateDeleted(loginPayload.getDateDeleted());
//        entity.setInbox(loginPayload.getInbox());
//        return entity;
//    }
//}
