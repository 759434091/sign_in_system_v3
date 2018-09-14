"use strict";

let oneStuSchedule;
let modal = {
    "title": "课程信息",
    "body": "                    <nav class=\"nav nav-tabs nav-fill\">\n" +
    "                        <a class=\"nav-item nav-link active\" href=\"javascript:void(0)\" id=\"coz_info_a\">课程信息</a>\n" +
    "                        <a class=\"nav-item nav-link\" href=\"javascript:void(0)\" id=\"hiz_sign_in_a\">历史签到</a>\n" +
    "                        <a class=\"nav-item nav-link\" href=\"javascript:void(0)\" id=\"his_leave_a\">历史请假</a>\n" +
    "                        <a class=\"nav-item nav-link\" href=\"javascript:void(0)\" id=\"his_absent_a\">历史缺勤</a>\n" +
    "                    </nav>\n" +
    "                    <div class=\"row\" id=\"coz_info\" style=\"display: none\">\n" +
    "                        <div class=\"col-12\">\n" +
    "                            <p class=\"mt-4 ml-2\"></p>\n" +
    "                        </div>\n" +
    "                    </div>\n" +
    "                    <div class=\"row\" id=\"his_sign_in\" style=\"display: none\">\n" +
    "                        <div class=\"col-12\">\n" +
    "                            <table class=\"table table-sm mt-1 text-center\">\n" +
    "                                <thead class=\"thead-light\">\n" +
    "                                <th>签到号</th>\n" +
    "                                <th>排课号</th>\n" +
    "                                <th>签到周</th>\n" +
    "                                <th>签到时间</th>\n" +
    "                                </thead>\n" +
    "                                <tbody class=\"table-striped\" id=\"his_sign_in_tBody\">\n" +
    "                                </tbody>\n" +
    "                            </table>\n" +
    "                        </div>\n" +
    "                    </div>\n" +
    "                    <div class=\"row\" id=\"his_leave\" style=\"display: none\">\n" +
    "                        <div class=\"col-12\">\n" +
    "                            <table class=\"table table-sm mt-1 text-center\">\n" +
    "                                <thead class=\"thead-light\">\n" +
    "                                <th>签到号</th>\n" +
    "                                <th>排课号</th>\n" +
    "                                <th>请假周</th>\n" +
    "                                <th>请假时间</th>\n" +
    "                                <th>审批状态</th>\n" +
    "                                </thead>\n" +
    "                                <tbody class=\"table-striped\" id=\"his_Leave_tBody\">\n" +
    "                                </tbody>\n" +
    "                            </table>\n" +
    "                        </div>\n" +
    "                    </div>\n" +
    "                    <div class=\"row\" id=\"his_absent\" style=\"display: none\">\n" +
    "                        <div class=\"col-12\">\n" +
    "                            <table class=\"table table-sm mt-1 text-center\">\n" +
    "                                <thead class=\"thead-light\">\n" +
    "                                <th>排课号</th>\n" +
    "                                <th>缺勤周</th>\n" +
    "                                <th>缺勤原因</th>\n" +
    "                                </thead>\n" +
    "                                <tbody class=\"table-striped\" id=\"his_absent_tBody\">\n" +
    "                                </tbody>\n" +
    "                            </table>\n" +
    "                        </div>\n" +
    "                    </div>",
    "action": function () {
        window.parent.$('#indexModal').modal('hide');
    }
};

function bindClick() {
    $("#course_form_submit").click(function () {
        selectCourseTable($('#sch_year').find('option:selected').val(), $('#sch_term').find('option:selected').val(), $('#sch_week').find('option:selected').val());
    });
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        let button = window.parent.$(this).data("button");
        let cozId = button.data('coz_id');
        window.parent.$("#modalTitle").text(modal.title);
        window.parent.$("#modalBody").empty().append(modal.body);
        window.parent.$("#modalOk").unbind('click').click(modal.action);
        window.parent.$("#coz_info_a").click(cozInfo);
        window.parent.$("#hiz_sign_in_a").click(hisSignIn);
        window.parent.$("#his_leave_a").click(hisLeave);
        window.parent.$("#his_absent_a").click(hisAbsent);
        turnModel(cozId);
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        $(this).removeData("button");
        window.parent.$("#modalBody").empty();
    });
}

function getCourseTable() {
    $.ajax({
        url: "../../Student/showCourses.do",
        async: true,
        headers: {
            'Access-Token': localStorage.getItem("token")
        },
        dataType: "json",
        success: function (data) {
            oneStuSchedule = data;
        },
        error: function (data) {
            console.log(data);
        }
    })
}

function selectCourseTable(year, term, week) {
    let selectCozSch = JSON.parse(JSON.stringify(oneStuSchedule));
    let fortnight = week % 2 === 0 ? 2 : 1;
    let courseList = selectCozSch.courses;
    for (let i = 0; i < courseList.length; i++) {
        let scheduleList = courseList[i].schedules;
        for (let j = 0; j < scheduleList.length; j++) {
            let schedule = scheduleList[j];
            if (schedule.schYear !== parseInt(year) ||
                schedule.schTerm.toString() !== term ||
                schedule.schWeek < parseInt(week) ||
                (schedule.schFortnight !== 0 && fortnight !== schedule.schFortnight)) {
                scheduleList[j] = null;
            }
        }
    }
    insertCourseTable(selectCozSch);
}

function insertCourseTable(selectCozSch) {
    $(".course-table tbody td").html("&nbsp;").removeAttr("rowspan").removeAttr("style").removeData("sch_id").unbind("click");
    let courseList = selectCozSch.courses;
    for (let i = 0; i < courseList.length; i++) {
        let course = courseList[i];
        let scheduleList = course.schedules;
        for (let j = 0; j < scheduleList.length; j++) {
            let schedule = scheduleList[j];
            if (schedule != null) {
                $(".course-table tbody tr").eq(schedule.schTime - 1).children("td").eq(schedule.schDay - 1).text(course.cozName).data("coz_id", schedule.cozId).click(function () {
                    window.parent.$('#indexModal').data("button", $(this)).modal('show');
                });
            }
        }
    }
    turnCourseTable();
}

function turnCourseTable() {
    let parent = $(".course-table tbody").children("tr");
    for (let i = 0; i < 12; i++) {
        for (let j = 0; j < 7; j++) {
            let test = parent.eq(i).children("td").eq(j);
            if (test.text().trim() === '') {
                continue;
            }
            for (let CountNum = 1, row = i + 1; CountNum < 12; CountNum++, row++) {
                let node = parent.eq(row).children("td").eq(j);
                if (test.text() !== node.text()) {
                    test.attr("rowspan", CountNum);
                    break;
                }
                node.hide();
            }
        }
    }
}

function turnModel(cozId) {
    window.parent.$("#his_Leave_tBody,#his_sign_in_tBody,#his_absent_tBody").empty();
    let courseList = oneStuSchedule.courses;
    let course = null;
    for (let i = 0; i < courseList.length; i++) {
        if (courseList[i].cozId === cozId) {
            course = courseList[i];
        }
    }
    if (course != null) {
        let scheduleList = course.schedules;
        let strCozInfo = "课程编号: " +
            course.cozId +
            "<br>课程名称: " +
            course.cozName +
            "<br>上课老师: " +
            course.teacher.userName +
            "<br>上课时间: ";
        let s1 = $("html");
        s1.css("cursor", "progress");
        (async function k() {
            for (let j = 0; j < scheduleList.length; j++) {
                let schedule = scheduleList[j];
                strCozInfo = strCozInfo +
                    "s" + schedule.schId + "&nbsp;&nbsp;&nbsp;总" +
                    toZhDigit(schedule.schWeek) +
                    "周&nbsp;&nbsp;&nbsp;星期" +
                    toZhDigit(schedule.schDay) +
                    "&nbsp;&nbsp;&nbsp;第" +
                    schedule.schTime +
                    "节<br>　　　　&nbsp;&nbsp;";
                await $.ajax(
                    {
                        type: "post",
                        url: "../../Student/fOneCozSignIn.do",
                        headers: {
                            'Access-Token': localStorage.getItem("token")
                        },
                        data: JSON.stringify(schedule),
                        contentType: "application/json;charset=utf-8",
                        async: true,
                        dataType: "json",
                        success: function (data) {
                            for (let k = 0; k < data.length; k++) {
                                let signInRes = data[k];
                                let time = signInRes.siTime[0] +
                                    "年&nbsp;&nbsp;" +
                                    toZhDigit(signInRes.siTime[1]) +
                                    "月&nbsp;&nbsp;" +
                                    (signInRes.siTime[2] < 10 ? "0" + signInRes.siTime[2] : signInRes.siTime[2]) +
                                    "日&nbsp;&nbsp;" +
                                    (signInRes.siTime[3] < 10 ? "0" + signInRes.siTime[3] : signInRes.siTime[3]) + ":" +
                                    (signInRes.siTime[4] < 10 ? "0" + signInRes.siTime[4] : signInRes.siTime[4]) + ":" +
                                    (signInRes.siTime[5] < 10 ? "0" + signInRes.siTime[5] : signInRes.siTime[5]);
                                if (signInRes.siLeave === false) {
                                    let tr = "<tr><th>" +
                                        signInRes.siId +
                                        "</th><td>" +
                                        "s" + schedule.schId +
                                        "</td><td>" +
                                        signInRes.siWeek +
                                        "</td><td>" +
                                        time +
                                        "</td></tr>";
                                    window.parent.$("#his_sign_in_tBody").append(tr);
                                } else if (signInRes.siLeave === true) {
                                    let statusMap = {
                                        0: "未审批",
                                        1: "通过",
                                        2: "未通过"
                                    };
                                    if (signInRes.siApprove === 1) {
                                        let tr = "<tr><th>" +
                                            signInRes.siId +
                                            "</th><td>" +
                                            "s" + schedule.schId +
                                            "</td><td>" +
                                            signInRes.siWeek +
                                            "</td><td>" +
                                            time +
                                            "</td><td>" +
                                            statusMap[signInRes.siApprove] +
                                            "</td></tr>";
                                        window.parent.$("#his_Leave_tBody").append(tr);
                                    } else {
                                        let tr = "<tr><td>" +
                                            "s" + schedule.schId +
                                            "</td><td>" +
                                            signInRes.siWeek +
                                            "</td><td>" +
                                            statusMap[signInRes.siApprove] +
                                            "</td></tr>";
                                        window.parent.$("#his_absent_tBody").append(tr);
                                        let tr2 = "<tr><th>" +
                                            signInRes.siId +
                                            "</th><td>" +
                                            "s" + schedule.schId +
                                            "</td>><td>" +
                                            signInRes.siWeek +
                                            "</td><td>" +
                                            time +
                                            "</td><td>" +
                                            statusMap[signInRes.siApprove] +
                                            "</td></tr>";
                                        window.parent.$("#his_Leave_tBody").append(tr2);
                                    }
                                }
                            }
                            $.ajax(
                                {
                                    type: "post",
                                    url: "../../Student/fOneCozAbsent.do",
                                    data: JSON.stringify(schedule),
                                    headers: {
                                        'Access-Token': localStorage.getItem("token")
                                    },
                                    contentType: "application/json;charset=utf-8",
                                    async: true,
                                    dataType: "json",
                                    success: function (data) {
                                        for (let k = 0; k < data.length; k++) {
                                            if (data[k] === -1) {
                                                let tr = "<tr><td>" +
                                                    "s" + schedule.schId +
                                                    "</td><td>" +
                                                    (k + 1) +
                                                    "</td><td>缺勤</td></tr>";
                                                window.parent.$("#his_absent_tBody").append(tr);
                                            }
                                        }
                                    },
                                    error: function (data) {
                                        console.log(data);
                                    }
                                }
                            );
                        },
                        error: function (data) {
                            console.log(data);
                        }
                    }
                );
            }
            window.parent.$("#coz_info").find("p").html(strCozInfo);
            s1.css("cursor", "default");
        })();
        window.parent.$("#coz_info_a").click();
    } else {
        alert("未知错误, 请稍后再试!");
    }
}

function cozInfo() {
    $(this).parent().children().removeClass("active");
    $(this).addClass("active");
    window.parent.$("#his_sign_in,#his_leave,#his_absent").hide();
    window.parent.$("#coz_info").show();
}

function hisSignIn() {
    $(this).parent().children().removeClass("active");
    $(this).addClass("active");
    window.parent.$("#coz_info,#his_leave,#his_absent").hide();
    window.parent.$("#his_sign_in").show();
}

function hisLeave() {
    $(this).parent().children().removeClass("active");
    $(this).addClass("active");
    window.parent.$("#his_sign_in,#coz_info,#his_absent").hide();
    window.parent.$("#his_leave").show();
}

function hisAbsent() {
    $(this).parent().children().removeClass("active");
    $(this).addClass("active");
    window.parent.$("#his_sign_in,#coz_info,#his_leave").hide();
    window.parent.$("#his_absent").show();
}

function toZhDigit(digit) {
    digit = typeof digit === 'number' ? String(digit) : digit;
    const zh = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九'];
    const unit = ['千', '百', '十', ''];
    const quot = ['万', '亿', '兆', '京', '垓', '秭', '穰', '沟', '涧', '正', '载', '极', '恒河沙', '阿僧祗', '那由他', '不可思议', '无量', '大数'];

    let breakLen = Math.ceil(digit.length / 4);
    let notBreakSegment = digit.length % 4 || 4;
    let segment;
    let zeroFlag = [], allZeroFlag = [];
    let result = '';

    while (breakLen > 0) {
        if (!result) {
            segment = digit.slice(0, notBreakSegment);
            let segmentLen = segment.length;
            for (let i = 0; i < segmentLen; i++) {
                if (segment[i] != 0) {
                    if (zeroFlag.length > 0) {
                        result += '零' + zh[segment[i]] + unit[4 - segmentLen + i];
                        // 判断是否需要加上 quot 单位
                        if (i === segmentLen - 1 && breakLen > 1) {
                            result += quot[breakLen - 2];
                        }
                        zeroFlag.length = 0;
                    } else {
                        result += zh[segment[i]] + unit[4 - segmentLen + i];
                        if (i === segmentLen - 1 && breakLen > 1) {
                            result += quot[breakLen - 2];
                        }
                    }
                } else {
                    // 处理为 0 的情形
                    if (segmentLen == 1) {
                        result += zh[segment[i]];
                        break;
                    }
                    zeroFlag.push(segment[i]);

                }
            }
        } else {
            segment = digit.slice(notBreakSegment, notBreakSegment + 4);
            notBreakSegment += 4;
            for (let j = 0; j < segment.length; j++) {
                if (segment[j] != 0) {
                    if (zeroFlag.length > 0) {
                        // 第一次执行zeroFlag长度不为0，说明上一个分区最后有0待处理
                        if (j === 0) {
                            result += quot[breakLen - 1] + zh[segment[j]] + unit[j];
                        } else {
                            result += '零' + zh[segment[j]] + unit[j];
                        }
                        zeroFlag.length = 0;
                    } else {
                        result += zh[segment[j]] + unit[j];
                    }
                    // 判断是否需要加上 quot 单位
                    if (j === segment.length - 1 && breakLen > 1) {
                        result += quot[breakLen - 2];
                    }
                } else {
                    // 第一次执行如果zeroFlag长度不为0, 且上一划分不全为0
                    if (j === 0 && zeroFlag.length > 0 && allZeroFlag.length === 0) {
                        result += quot[breakLen - 1];
                        zeroFlag.length = 0;
                        zeroFlag.push(segment[j]);
                    } else if (allZeroFlag.length > 0) {
                        // 执行到最后
                        if (breakLen == 1) {
                            result += '';
                        } else {
                            zeroFlag.length = 0;
                        }
                    } else {
                        zeroFlag.push(segment[j]);
                    }
                    if (j === segment.length - 1 && zeroFlag.length === 4 && breakLen !== 1) {
                        // 如果执行到末尾
                        if (breakLen === 1) {
                            allZeroFlag.length = 0;
                            zeroFlag.length = 0;
                            result += quot[breakLen - 1];
                        } else {
                            allZeroFlag.push(segment[j]);
                        }
                    }
                }
            }
            --breakLen;
        }
        return result;
    }
}