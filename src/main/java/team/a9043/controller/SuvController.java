package team.a9043.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.a9043.mvcaop.TokenUser;
import team.a9043.pojo.*;
import team.a9043.service.LoginService;
import team.a9043.service.SuvService;

import java.time.LocalDateTime;
import java.util.List;

@Controller("suvController")
@RequestMapping("/Supervisor")
public class SuvController {
    private final SuvService suvService;

    @Autowired
    public SuvController(SuvService suvService, LoginService loginService) {
        this.suvService = suvService;
    }

    /**
     * 查看所有监督课程findToBeSupervised
     *
     * @return 返回 课程列表
     */
    @ResponseBody
    @RequestMapping("/showSuvCourses")
    public List<SuvSch> showSuvCourses(@TokenUser User user) {
        return suvService.showSuvCourses(user.getUserId());
    }

    /**
     * 查看正在监督的课程
     *
     * @return 返回 一个课程
     */
    @ResponseBody
    @RequestMapping("/showOneSuvCoz")
    public SuvSch showOneSuvCoz(@TokenUser User user) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return suvService.findOneSuvCoz(user.getUserId(), localDateTime);
    }

    /**
     * 插入一条督导记录
     *
     * @param suvRecord 督导记录
     * @return 返回成功与否
     */
    @ResponseBody
    @RequestMapping("/insertSuvRec")
    public Boolean insertSuvRec(@RequestBody SuvRecord suvRecord) {
        return suvService.insertSuvRec(suvRecord);
    }

    /**
     * 检查是否已经有督导记录
     *
     * @return 返回是否
     */
    @ResponseBody
    @RequestMapping("/isSuvRec")
    public Boolean isSuvRec(@TokenUser User user) {
        LocalDateTime localDateTime = LocalDateTime.now();
        SuvSch suvSch = suvService.findOneSuvCoz(user.getUserId(), localDateTime);
        if (suvSch == null) {
            return false;
        } else {
            return suvService.isSuvRec(suvSch.getSuvId());
        }
    }

    /**
     * 返回请假记录
     *
     * @return 返回请假记录
     */
    @ResponseBody
    @RequestMapping("/getLeaves")
    public List<SignInRes> getLeaves(@TokenUser User user) {
        return suvService.getLeaves(user.getUserId());
    }


    @ResponseBody
    @RequestMapping("/approveLeave")
    public Boolean approveLeave(@TokenUser User user, @RequestBody SignInRes signInRes) {
        return suvService.approveLeave(user, signInRes);
    }

    @ResponseBody
    @RequestMapping("/rejectLeave")
    public Boolean rejectLeave(@TokenUser User user, @RequestBody SignInRes signInRes) {
        return suvService.rejectLeave(user, signInRes);
    }

    /**
     * 查看历史督导记录
     *
     * @return 返回记录
     */
    @ResponseBody
    @RequestMapping("/findHisSuvRecRes")
    public List<HisSuvRecRes> findHisSuvRecRes(@TokenUser User user) {
        return suvService.findHisSuvRecRes(user);
    }

    @ResponseBody
    @RequestMapping("/suvLeave")
    public Boolean suvLeave(@TokenUser User user, @RequestBody SuvSch suvSch) {
        return suvService.suvLeave(user, suvSch);
    }

    @ResponseBody
    @RequestMapping("/getSignIn")
    public SuvMan getSignIn(@RequestBody SuvMan suvMan) {
        return suvService.getSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/initManSignIn")
    public Boolean initManSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return suvService.checkPower(suvMan).equals(user.getUserId()) && suvService.initManSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/initAutoSignIn")
    public Boolean initAutoSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return suvService.checkPower(suvMan).equals(user.getUserId()) && suvService.initAutoSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/closeAutoSignIn")
    public Boolean closeAutoSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return suvService.checkPower(suvMan).equals(user.getUserId()) && suvService.closeAutoSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/openAutoSignIn")
    public Boolean openAutoSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return suvService.checkPower(suvMan).equals(user.getUserId()) && suvService.openAutoSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/closeManSignIn")
    public Boolean closeManSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return suvService.checkPower(suvMan).equals(user.getUserId()) && suvService.closeManSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/openManSignIn")
    public Boolean openManSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return suvService.checkPower(suvMan).equals(user.getUserId()) && suvService.openManSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/cancelSignIn")
    public Boolean cancelSignIn(@TokenUser User user, @RequestBody SuvMan suvMan) {
        return suvService.checkPower(suvMan).equals(user.getUserId()) && suvService.cancelSignIn(suvMan);
    }

    @ResponseBody
    @RequestMapping("/findToBeSupervised")
    public List<SuvSch> findToBeSupervised() {
        return suvService.findToBeSupervised();
    }

    @ResponseBody
    @RequestMapping("/receiveSuvSch")
    public Boolean receiveSuvSch(@TokenUser User user, @RequestBody SuvSch suvSch) {
        suvSch.setStudent(user);
        return suvService.receiveSuvSch(suvSch);
    }

    @ResponseBody
    @RequestMapping("/giveUpPower")
    public Boolean giveUpPower(@TokenUser User user, @RequestBody SuvSch suvSch) {
        return suvService.giveUpPower(user, suvSch);
    }

    @ResponseBody
    @RequestMapping("/applyForTrans")
    public Boolean applyForTrans(@TokenUser User user, @RequestBody SuvTrans suvTrans) {
        return user.getUserId().equals(suvTrans.getSuvSch().getStudent().getUserId()) && suvService.applyForTrans(suvTrans);
    }

    @ResponseBody
    @RequestMapping("/acceptSuvTrans")
    public Boolean acceptSuvTrans(@TokenUser User user, @RequestBody SuvTrans suvTrans) {
        return user.getUserId().equals(suvTrans.getUserId()) && suvService.acceptSuvTrans(user, suvTrans);
    }

    @ResponseBody
    @RequestMapping("/refuseSuvTrans")
    public Boolean refuseSuvTrans(@TokenUser User user, @RequestBody SuvTrans suvTrans) {
        return user.getUserId().equals(suvTrans.getUserId()) && suvService.refuseSuvTrans(suvTrans);
    }

    @ResponseBody
    @RequestMapping("/getSuvTrans")
    public List<SuvTrans> getSuvTrans(@TokenUser User user) {
        return suvService.getSuvTrans(user);
    }

}
