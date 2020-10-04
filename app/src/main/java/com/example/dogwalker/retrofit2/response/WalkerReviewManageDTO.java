package com.example.dogwalker.retrofit2.response;

public class WalkerReviewManageDTO {

    //리뷰 관련
    int review_idx;
    int review_booking_id;
    String review_owner_id;
    String review_memo;
//    String review_files_url;
    String review_created_date;
    //답변 관련
    int reply_idx;
    int reply_review_id;
    String reply_walker_id;
    String reply_memo;
    String reply_created_date;
    String reply_updated_date;
    String reply_deleted_date;

    public WalkerReviewManageDTO(int review_idx, int review_booking_id, String review_owner_id, String review_memo, String review_created_date, int reply_idx, int reply_review_id, String reply_walker_id, String reply_memo, String reply_created_date, String reply_updated_date, String reply_deleted_date) {
        this.review_idx = review_idx;
        this.review_booking_id = review_booking_id;
        this.review_owner_id = review_owner_id;
        this.review_memo = review_memo;
        this.review_created_date = review_created_date;
        this.reply_idx = reply_idx;
        this.reply_review_id = reply_review_id;
        this.reply_walker_id = reply_walker_id;
        this.reply_memo = reply_memo;
        this.reply_created_date = reply_created_date;
        this.reply_updated_date = reply_updated_date;
        this.reply_deleted_date = reply_deleted_date;
    }

    public int getReview_idx() {
        return review_idx;
    }

    public void setReview_idx(int review_idx) {
        this.review_idx = review_idx;
    }

    public int getReview_booking_id() {
        return review_booking_id;
    }

    public void setReview_booking_id(int review_booking_id) {
        this.review_booking_id = review_booking_id;
    }

    public String getReview_owner_id() {
        return review_owner_id;
    }

    public void setReview_owner_id(String review_owner_id) {
        this.review_owner_id = review_owner_id;
    }

    public String getReview_memo() {
        return review_memo;
    }

    public void setReview_memo(String review_memo) {
        this.review_memo = review_memo;
    }

    public String getReview_created_date() {
        return review_created_date;
    }

    public void setReview_created_date(String review_created_date) {
        this.review_created_date = review_created_date;
    }

    public int getReply_idx() {
        return reply_idx;
    }

    public void setReply_idx(int reply_idx) {
        this.reply_idx = reply_idx;
    }

    public int getReply_review_id() {
        return reply_review_id;
    }

    public void setReply_review_id(int reply_review_id) {
        this.reply_review_id = reply_review_id;
    }

    public String getReply_walker_id() {
        return reply_walker_id;
    }

    public void setReply_walker_id(String reply_walker_id) {
        this.reply_walker_id = reply_walker_id;
    }

    public String getReply_memo() {
        return reply_memo;
    }

    public void setReply_memo(String reply_memo) {
        this.reply_memo = reply_memo;
    }

    public String getReply_created_date() {
        return reply_created_date;
    }

    public void setReply_created_date(String reply_created_date) {
        this.reply_created_date = reply_created_date;
    }

    public String getReply_updated_date() {
        return reply_updated_date;
    }

    public void setReply_updated_date(String reply_updated_date) {
        this.reply_updated_date = reply_updated_date;
    }

    public String getReply_deleted_date() {
        return reply_deleted_date;
    }

    public void setReply_deleted_date(String reply_deleted_date) {
        this.reply_deleted_date = reply_deleted_date;
    }
}
