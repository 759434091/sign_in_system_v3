package team.a9043.sign_in_system.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SisSignIn {
    private Integer ssiId;

    private Integer ssId;

    private Integer ssiWeek;

    private LocalDateTime ssiCreateTime;

    private Double ssiAttRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<SisSignInDetail> sisSignInDetailList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public SisSchedule sisSchedule;
}