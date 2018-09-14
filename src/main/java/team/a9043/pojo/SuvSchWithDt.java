package team.a9043.pojo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 带有JSON时间对象的一节督导课程
 */
public class SuvSchWithDt {
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime localDateTime;
    private SuvSch suvSch;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public SuvSchWithDt setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public SuvSch getSuvSch() {
        return suvSch;
    }

    public SuvSchWithDt setSuvSch(SuvSch suvSch) {
        this.suvSch = suvSch;
        return this;
    }
}
