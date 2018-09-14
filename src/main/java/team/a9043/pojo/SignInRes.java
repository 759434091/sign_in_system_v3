package team.a9043.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 签到 类
 */
public class SignInRes {
    private int siId;
    private User student;
    private OneCozAndSch oneCozAndSch;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime siTime;
    private int siWeek;
    private boolean siLeave;
    private int siApprove;
    private byte[] siVoucher;

    public int getSiId() {
        return siId;
    }

    public SignInRes setSiId(int siId) {
        this.siId = siId;
        return this;
    }

    public User getStudent() {
        return student;
    }

    public SignInRes setStudent(User student) {
        this.student = student;
        return this;
    }

    public OneCozAndSch getOneCozAndSch() {
        return oneCozAndSch;
    }

    public SignInRes setOneCozAndSch(OneCozAndSch oneCozAndSch) {
        this.oneCozAndSch = oneCozAndSch;
        return this;
    }

    public LocalDateTime getSiTime() {
        return siTime;
    }

    public SignInRes setSiTime(LocalDateTime siTime) {
        this.siTime = siTime;
        return this;
    }

    public int getSiWeek() {
        return siWeek;
    }

    public SignInRes setSiWeek(int siWeek) {
        this.siWeek = siWeek;
        return this;
    }

    public boolean isSiLeave() {
        return siLeave;
    }

    public SignInRes setSiLeave(boolean siLeave) {
        this.siLeave = siLeave;
        return this;
    }

    public int getSiApprove() {
        return siApprove;
    }

    public SignInRes setSiApprove(int siApprove) {
        this.siApprove = siApprove;
        return this;
    }

    public byte[] getSiVoucher() {
        return siVoucher;
    }

    public SignInRes setSiVoucher(byte[] siVoucher) {
        this.siVoucher = siVoucher;
        return this;
    }
}
