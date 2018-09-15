package team.a9043.sign_in_system.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SisJoinCourse {
    public enum JoinCourseType {
        ATTENDANCE(), TEACHING()
    }

    private Integer sjcId;

    private Integer joinCourseType;

    private String scId;

    private String suId;

    private SisUser sisUser;

    private SisCourse sisCourse;

    @Override
    public int hashCode() {
        return String.format("SisJoinCourse_%s_%s", scId, suId).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof SisJoinCourse))
            return false;
        SisJoinCourse sisJoinCourse = (SisJoinCourse) obj;
        return suId.equals(sisJoinCourse.suId) &&
            scId.equals(sisJoinCourse.scId) &&
            joinCourseType.equals(sisJoinCourse.joinCourseType);
    }
}