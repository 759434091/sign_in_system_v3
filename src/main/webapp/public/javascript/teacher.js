let oneTchSchedule;

let modalMode = {
    "showCozStu": {
        "title": "查看学生",
        "body": "<table class=\"table table-sm table-dark table-striped table-hover tch-table text-center m-0\">\n" +
        "    <thead>\n" +
        "    <tr>\n" +
        "        <th>学号</th>\n" +
        "        <th>姓名</th>\n" +
        "    </tr>\n" +
        "    </thead>\n" +
        "    <tbody id=\"tch_coz_stu\">\n" +
        "    </tbody>\n" +
        "</table>",
        "action": function () {
            window.parent.$('#indexModal').modal('hide');
        }
    },
    "suspendCourse": {
        "title": "设置停课",
        "body": "<form action='javascript:void(0)'>\n" +
        "            <div class=\"form-group\">" +
        "                <select class=\"form-control\" id=\"schId\">\n" +
        "                </select>" +
        "            </div>" +
        "            <div class=\"form-group\">\n" +
        "                 <input id=\"susWeek\" name='susWeek' class=\"form-control\" type=\"text\" placeholder='请输入停课周'>\n" +
        "            </div>\n" +
        "        </form>",
        "action": suspendCourse
    },
    "setCozSuv": {
        "title": "设置督导",
        "body": "<form action='javascript:void(0)'>\n" +
        "            <div class=\"form-group\">" +
        "                <select class=\"form-control\" id=\"schId\">\n" +
        "                </select>" +
        "            </div>" +
        "            <div class=\"form-group\">\n" +
        "                 <input id=\"suvWeek\" name='suvWeek' class=\"form-control\" type=\"text\" placeholder='请输入督导周'>\n" +
        "            </div>\n" +
        "        </form>",
        "action": setCozSuv
    },
    "removeCozSuv": {
        "title": "取消督导",
        "body": "<form action='javascript:void(0)'>\n" +
        "            <div class=\"form-group\">" +
        "                <select class=\"form-control\" id=\"schId\" onchange='window.frames[0].getCozSuv()'>\n" +
        "                    <option disabled selected>请选择排课号</option>" +
        "                </select>" +
        "            </div>" +
        "            <div class=\"form-group\">\n" +
        "                <select class=\"form-control\" id=\"suvWeek\">\n" +
        "                    <option disabled selected>请选择周</option>" +
        "                </select>" +
        "            </div>\n" +
        "        </form>",
        "action": removeCozSuv
    },
    "setCozSignIn": {
        "title": "设置签到",
        "body": "<form action='javascript:void(0)'>\n" +
        "            <div class=\"form-group\">" +
        "                <select class=\"form-control\" id=\"schId\">\n" +
        "                </select>" +
        "            </div>" +
        "            <div class=\"form-group\">\n" +
        "                 <input id=\"siWeek\" name='siWeek' class=\"form-control\" type=\"text\" placeholder='请输入签到周'>\n" +
        "            </div>\n" +
        "            <div class=\"form-group\">\n" +
        "                <select class=\"form-control\" id=\"signInChoice\" onchange='window.frames[0].changeSignInMethod()'>\n" +
        "                    <option disabled selected>请选择方式</option>" +
        "                    <option value='auto'>自动签到</option>" +
        "                    <option value='manual'>人工签到</option>" +
        "                    <option value='both'>同时开启</option>" +
        "                </select>" +
        "            </div>\n" +
        "            <div id='timePicker' class=\"form-group\" style='display: none'>" +
        "                <label for=\"datetimeInput\">请选择时间</label>" +
        "                <input id=\"datetimeInput\" type=\"datetime-local\" class=\"form-control\" value=\"\">" +
        "                <small id=\"datetimeHelp\" class=\"form-text text-muted\">发起时间至后十分钟有效" +
        "                </small>" +
        "            </div>" +
        "        </form>",
        "action": setCozSignIn
    },
    "removeCozSignIn": {
        "title": "取消签到",
        "body": "<form action='javascript:void(0)'>\n" +
        "            <div class=\"form-group\">" +
        "                <select class=\"form-control\" id=\"schId\" onchange='window.frames[0].getCozSignIn()'>\n" +
        "                    <option disabled selected>请选择排课号</option>" +
        "                </select>" +
        "            </div>" +
        "            <div class=\"form-group\">\n" +
        "                <select class=\"form-control\" id=\"siWeek\">\n" +
        "                    <option disabled selected>请选择周</option>" +
        "                </select>" +
        "            </div>\n" +
        "        </form>",
        "action": removeCozSignIn
    }
};

$(function () {
    getTchCourse();
});

function getTchCourse() {
    $("html").css("cursor", "progress");
    $.ajax({
        type: "post",
        url: "../../Teacher/showTchCourses.do",
        contentType: "application/json;charset=utf-8",
        headers: {
            'Access-Token': localStorage.getItem("token")
        },
        async: false,
        dataType: "json",
        success: function (data) {
            oneTchSchedule = data;
            $("html").css("cursor", "default");
        },
        error: function (err) {
            console.log(err);
            oneTchSchedule = null;
            $("html").css("cursor", "default");
        }
    })
}

function showTchCourse() {
    let courseList = oneTchSchedule.courses;
    for (let i = 0; i < courseList.length; i++) {
        let course = courseList[i];
        let tBodyStr = `<tr><th scope='row'>${course.cozId}</th><td>${course.cozName}</td><td>${oneTchSchedule.teacher.userName}</td><td>` +
            `<div id='dropdown_${course.cozId}'><i class='fa fa-cog' role='button' id='dropdownMenuButton_${course.cozId}' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'></i>` +
            `<div class='dropdown-menu' aria-labelledby='dropdownMenuButton_${course.cozId}'>` +
            `<a id='show_student_${course.cozId}' class='dropdown-item font-weight-bold' href='javascript:void(0)'>查看学生</a>` +
            `<a id='coz_suspend_${course.cozId}' class='dropdown-item font-weight-bold' href='javascript:void(0)'>设置停课</a>` +
            `<a id='coz_suv_${course.cozId}' class='dropdown-item font-weight-bold' href='javascript:void(0)'>设置督导</a>` +
            `<a id='coz_rm_suv_${course.cozId}' class='dropdown-item font-weight-bold' href='javascript:void(0)'>取消督导</a>` +
            `<a id='coz_sign_in_${course.cozId}' class='dropdown-item font-weight-bold' href='javascript:void(0)'>设置签到</a>` +
            `<a id='coz_rm_sign_in_${course.cozId}' class='dropdown-item font-weight-bold' href='javascript:void(0)'>取消签到</a>` +
            `</div></div></td></tr>`;
        $("#tch_course").append(tBodyStr);
        $("#show_student_" + replacePoint(course.cozId)).data("coz_id", course.cozId).data("mode", "showCozStu").click(function () {
            window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
                let button = window.parent.$(this).data("button");
                let cozId = button.data('coz_id');
                let mode = button.data('mode');
                window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
                window.parent.$("#modalTitle").text(modalMode[mode].title);
                window.parent.$("#modalBody").empty().append(modalMode[mode].body);
                window.parent.$("#modalOk").unbind('click').click(modalMode[mode].action);//tch_coz_stu
                $.ajax({
                    type: "get",
                    url: "../../Teacher/getCozStudent.do",
                    data: {
                        cozId: course.cozId
                    },
                    headers: {
                        'Access-Token': localStorage.getItem("token")
                    },
                    async: false,
                    dataType: "json",
                    success: function (data) {
                        for (let i = 0; i < data.length; i++) {
                            let user = data[i];
                            window.parent.$("#tch_coz_stu").append(`<tr><td>${user.userId}</td><td>${user.userName}</td></tr>`)
                        }
                    },
                    error: function (err) {
                        console.log(err);
                    }
                })
            }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
                window.parent.$(this).removeData("coz_id").removeData("button");
                window.parent.$("#modalBody").empty();
            });
            window.parent.$('#indexModal').data("button", $(this)).modal('show');
        });
        $("#coz_suspend_" + replacePoint(course.cozId)).data("coz_id", course.cozId).data("mode", "suspendCourse").click(function () {
            window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
                let button = window.parent.$(this).data("button");
                let cozId = button.data('coz_id');
                let mode = button.data('mode');
                window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
                window.parent.$("#modalTitle").text(modalMode[mode].title);
                window.parent.$("#modalBody").empty().append(modalMode[mode].body);
                window.parent.$("#modalOk").unbind('click').click(modalMode[mode].action);
                changeCozSusSch(cozId);
            }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
                window.parent.$(this).removeData("coz_id").removeData("button");
                window.parent.$("#modalBody").empty();
            });
            window.parent.$('#indexModal').data("button", $(this)).modal('show');
        });
        $("#coz_suv_" + replacePoint(course.cozId)).data("coz_id", course.cozId).data("mode", "setCozSuv").click(function () {
            window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
                let button = window.parent.$(this).data("button");
                let cozId = button.data('coz_id');
                let mode = button.data('mode');
                window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
                window.parent.$("#modalTitle").text(modalMode[mode].title);
                window.parent.$("#modalBody").empty().append(modalMode[mode].body);
                window.parent.$("#modalOk").unbind('click').click(modalMode[mode].action);
                changeCozSusSch(cozId);
            }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
                window.parent.$(this).removeData("coz_id").removeData("button");
                window.parent.$("#modalBody").empty();
            });
            window.parent.$('#indexModal').data("button", $(this)).modal('show');
        });
        $("#coz_rm_suv_" + replacePoint(course.cozId)).data("coz_id", course.cozId).data("mode", "removeCozSuv").click(function () {
            window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
                let button = window.parent.$(this).data("button");
                let cozId = button.data('coz_id');
                let mode = button.data('mode');
                window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
                window.parent.$("#modalTitle").text(modalMode[mode].title);
                window.parent.$("#modalBody").empty().append(modalMode[mode].body);
                window.parent.$("#modalOk").unbind('click').click(modalMode[mode].action);
                changeCozSusSch(cozId);
            }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
                window.parent.$(this).removeData("coz_id").removeData("button");
                window.parent.$("#modalBody").empty();
            });
            window.parent.$('#indexModal').data("button", $(this)).modal('show');
        });
        $("#coz_sign_in_" + replacePoint(course.cozId)).data("coz_id", course.cozId).data("mode", "setCozSignIn").click(function () {
            window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
                let button = window.parent.$(this).data("button");
                let cozId = button.data('coz_id');
                let mode = button.data('mode');
                window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
                window.parent.$("#modalTitle").text(modalMode[mode].title);
                window.parent.$("#modalBody").empty().append(modalMode[mode].body);
                window.parent.$("#modalOk").unbind('click').click(modalMode[mode].action);
                changeCozSusSch(cozId);
            }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
                window.parent.$(this).removeData("coz_id").removeData("button");
                window.parent.$("#modalBody").empty();
            });
            window.parent.$('#indexModal').data("button", $(this)).modal('show');
        });
        $("#coz_rm_sign_in_" + replacePoint(course.cozId)).data("coz_id", course.cozId).data("mode", "removeCozSignIn").click(function () {
            window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
                let button = window.parent.$(this).data("button");
                let cozId = button.data('coz_id');
                let mode = button.data('mode');
                window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
                window.parent.$("#modalTitle").text(modalMode[mode].title);
                window.parent.$("#modalBody").empty().append(modalMode[mode].body);
                window.parent.$("#modalOk").unbind('click').click(modalMode[mode].action);
                changeCozSusSch(cozId);
            }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
                window.parent.$(this).removeData("coz_id").removeData("button");
                window.parent.$("#modalBody").empty();
            });
            window.parent.$('#indexModal').data("button", $(this)).modal('show');
        });
    }
}

function initHisRecForm() {
    let courseList = oneTchSchedule.courses;
    for (let i = 0; i < courseList.length; i++) {
        let course = courseList[i];
        let str = "<option value='" + course.cozId + "'>" + course.cozName + "</option>";
        $("#coz_name").append(str);
    }
}

function changeSchedule(cozId) {
    let s1 = $("#coz_schedule");
    s1.empty().append("<option selected>请选择</option>");
    let schedules = null;
    for (let i = 0; i < oneTchSchedule.courses.length; i++) {
        if (oneTchSchedule.courses[i].cozId === cozId) {
            schedules = oneTchSchedule.courses[i].schedules;
            break;
        }
    }
    if (schedules != null) {
        schedules.sort(function (a, b) {
            if (a.schYear === b.schYear) {
                if (a.schTerm === b.schTerm) {
                    if (a.schDay === b.schDay) {
                        return a.schTime - b.schTime;
                    } else {
                        return a.schDay - b.schDay;
                    }
                } else {
                    return Number(a.schTerm) - Number(b.schTerm);
                }
            } else {
                return a.schYear - b.schYear;
            }
        });
        for (let i = 0; i < schedules.length; i++) {
            let schJoin = toZhDigit(schedules[i].schTime);
            let schId = schedules[i].schId;
            while ((i !== schedules.length - 1) &&
            (schedules[i + 1].schYear === schedules[i].schYear) &&
            (schedules[i + 1].schTerm === schedules[i].schTerm) &&
            (schedules[i + 1].schWeek === schedules[i].schWeek) &&
            (schedules[i + 1].schFortnight === schedules[i].schFortnight) &&
            (schedules[i + 1].schDay === schedules[i].schDay) &&
            (schedules[i + 1].schTime - schedules[i].schTime === 1)) {
                schJoin += "、" + toZhDigit(schedules[++i].schTime);
            }
            let str = `<option value='${schId}'>星期${toZhDigit(schedules[i].schDay)}第${schJoin}节</option>`;
            s1.append(str)
        }
    }

}

function getHisSuvRecRes(schId) {
    let schedule = null;
    let breakNum = false;
    let courseList = oneTchSchedule.courses;
    for (let i = 0; i < courseList.length; i++) {
        if (breakNum === false) {
            let schedules = courseList[i].schedules;
            for (let j = 0; j < schedules.length; j++) {
                if (schedules[j].schId = schId) {
                    schedule = schedules[j];
                    breakNum = true;
                    break;
                }
            }
        } else {
            break;
        }
    }
    $.ajax({
            type: "post",
            url: "../../Teacher/fSuvRecByCoz.do",
            data: JSON.stringify(schedule),
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function (data) {
                showHisSuvRecRes(data);
            },
            error: function (data) {
                console.log(data)
            }
        }
    );
}

function showHisSuvRecRes(hisSuvRecList) {
    let s1 = $("#tch_rec");
    s1.empty();
    for (let i = 0; i < hisSuvRecList.length; i++) {
        let hisSuvRec = hisSuvRecList[i];
        let str = "<tr><th scope='row'>" + hisSuvRec.suvId + "</th><td>第" +
            hisSuvRec.suvWeek +
            "周</td><td>" +
            hisSuvRec.suvRecName +
            "</td><td>" +
            hisSuvRec.suvRecNum +
            "</td><td>" +
            hisSuvRec.suvRecBadNum +
            "</td></tr>";
        s1.empty().append(str);
    }
}

function getHisAbsentRes(schId, week) {
    let schedule = null;
    let breakNum = false;
    let courseList = oneTchSchedule.courses;
    for (let i = 0; i < courseList.length; i++) {
        if (breakNum === false) {
            let schedules = courseList[i].schedules;
            for (let j = 0; j < schedules.length; j++) {
                if (schedules[j].schId = schId) {
                    schedule = schedules[j];
                    breakNum = true;
                    break;
                }
            }
        } else {
            break;
        }
    }
    $.ajax({
            type: "get",
            url: "../../Teacher/fSchAbsRecByCoz.do",
            data: {
                schId: schedule.schId,
                week: week
            },
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                showHisAbsentRec(data);
            },
            error: function (data) {
                console.log(data)
            }
        }
    );
}

function showHisAbsentRec(hisAbsRecList) {
    let s1 = $("#tch_abs_rec");
    s1.empty();
    for (let i = 0; i < hisAbsRecList.length; i++) {
        let str = "";
        let hisAbsRec = hisAbsRecList[i];
        let studentList = hisAbsRecList[i].studentList;
        let stuStr = "";
        for (let j = 0; j < studentList.length; j++) {
            stuStr = `${stuStr + studentList[j].userId + studentList[j].userName},　<br>`;
        }
        stuStr = stuStr.substring(0, stuStr.length - 2);
        str = "<tr><th scope='row'>第" +
            hisAbsRec.sarWeek +
            "周</th><td>" +
            stuStr +
            "</td></tr>";
        s1.append(str);
    }
}

function getLeaves() {
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/getLeaves.do",
            contentType: "application/json;charset=utf-8",
            async: true,
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            dataType: "json",
            success: function (data) {
                signInResList = data;
                showLeaves(signInResList);
                $('[data-toggle="popover"]').popover({
                    trigger: 'hover',
                    html: true,
                });
                $(".popover-tr").hover(function () {
                    $(this).parent().children(".popover-content").popover('show');
                }, function () {
                    $(this).parent().children(".popover-content").popover('hide');
                });
            },
            error: function (data) {
                console.log(data);
            }
        }
    );
}

function showLeaves(signInResList) {
    for (let i = 0; i < signInResList.length; i++) {
        let signInRes = signInResList[i];
        let oneCozAndSch = signInRes.oneCozAndSch;
        let imageByte = signInRes.siVoucher;
        let popoverStr = `<img width="100%" src="data:image/jpg;base64,${imageByte}">`;
        let tr = `<tr><th class='popover-tr popover-content align-middle' data-toggle='popover' data-placement='top' data-content='${popoverStr}' scope="row">${signInRes.siId}</th>` +
            `<td>${oneCozAndSch.course.cozName}<br>${oneCozAndSch.course.teacher.userName}</td>` +
            `<td>第${toZhDigit(signInRes.siWeek)}周&nbsp;&nbsp;星期${toZhDigit(oneCozAndSch.schedule.schDay)}<br>` +
            `第${oneCozAndSch.schedule.schTime}节</td><td>${signInRes.student.userId}<br>${signInRes.student.userName}</td>` +
            `<td>${signInRes.siTime[0]}-${signInRes.siTime[1] < 10 ? "0" + signInRes.siTime[1] : signInRes.siTime[1]}-${signInRes.siTime[2] < 10 ? "0" + signInRes.siTime[2] : signInRes.siTime[2]}` +
            `<br>${signInRes.siTime[3] < 10 ? "0" + signInRes.siTime[3] : signInRes.siTime[3]}:${signInRes.siTime[4] < 10 ? "0" + signInRes.siTime[4] : signInRes.siTime[4]}:${signInRes.siTime[5] < 10 ? "0" + signInRes.siTime[5] : signInRes.siTime[5]}</td>` +
            `<td class='btn-group' id='btn_group_${signInRes.siId}'><button class='btn btn-success btn-sm' id='leave_btn_approve_${signInRes.siId}' onclick='approveLeave(${signInRes.siId})'>通过</button>` +
            `<br><button class='btn btn-danger btn-sm' id='leave_btn_reject_${signInRes.siId}' onclick='rejectLeave(${signInRes.siId})'>驳回</button></td></tr>`;
        $("#tch_leave").append(tr);
        let id = "#btn_group_" + signInRes.siId;
        if (signInRes.siApprove == 1) {
            $(id).empty().text("已通过");
        } else if (signInRes.siApprove == 2) {
            $(id).empty().text("已驳回");
        }
    }
}

function suspendCourse() {
    let susWeek = window.parent.$("#susWeek").val();
    let schId = window.parent.$("#schId").find('option:selected').val().substring(1);
    let paramStr = schId + "&" + susWeek;
    $.ajax({
        type: "post",
        url: "../../Teacher/suspendClass.do",
        data: paramStr,
        contentType: "application/json;charset=utf-8",
        async: true,
        headers: {
            'Access-Token': localStorage.getItem("token")
        },
        dataType: "json",
        success: function (data) {
            if (data === true) {
                alert("设置成功!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            } else {
                alert("设置失败!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        },
        error: function (err) {
            console.log(err);
            alert("设置失败!");
            location.href = location.href;
            window.parent.$('#indexModal').modal('hide');
        }
    })

}

function changeCozSusSch(cozId) {
    if (oneTchSchedule != null) {
        let courseList = oneTchSchedule.courses;
        let course = null;
        for (let i = 0; i < courseList.length; i++) {
            if (courseList[i].cozId == cozId) {
                course = courseList[i];
                break;
            }
        }
        if (course != null) {
            let scheduleList = course.schedules;
            scheduleList.sort(function (a, b) {
                if (a.schYear === b.schYear) {
                    if (a.schTerm === b.schTerm) {
                        if (a.schDay === b.schDay) {
                            return a.schTime - b.schTime;
                        } else {
                            return a.schDay - b.schDay;
                        }
                    } else {
                        return Number(a.schTerm) - Number(b.schTerm);
                    }
                } else {
                    return a.schYear - b.schYear;
                }
            });
            for (let i = 0; i < scheduleList.length; i++) {
                let schJoin = toZhDigit(scheduleList[i].schTime);
                let schId = scheduleList[i].schId;
                while ((i !== scheduleList.length - 1) &&
                (scheduleList[i + 1].schYear === scheduleList[i].schYear) &&
                (scheduleList[i + 1].schTerm === scheduleList[i].schTerm) &&
                (scheduleList[i + 1].schWeek === scheduleList[i].schWeek) &&
                (scheduleList[i + 1].schFortnight === scheduleList[i].schFortnight) &&
                (scheduleList[i + 1].schDay === scheduleList[i].schDay) &&
                (scheduleList[i + 1].schTime - scheduleList[i].schTime === 1)) {
                    schJoin += "、" + toZhDigit(scheduleList[++i].schTime);
                }
                let str = `<option value='s${schId}'>s${schId}&emsp;` +
                    `星期${toZhDigit(scheduleList[i].schDay)}第${schJoin}节</option>`;
                window.parent.$("#schId").append(str);
            }
        }
    }
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
                    continue;
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
                    continue;
                }
            }
            --breakLen;
        }
        return result;
    }
}

function submitCozForm() {
    let cozImport = $("#insertCozInfo").serializeJSON();
    $.ajax({
        type: "post",
        url: "../../File/insertCozSch.do",
        data: JSON.stringify(cozImport),
        contentType: "application/json;charset=utf-8",
        headers: {
            'Access-Token': localStorage.getItem("token")
        },
        async: false,
        dataType: "json",
        success: function (data) {
            alert(data);
        },
        error: function (err) {
            console.log(err);
        }
    })
}

function submitStuForm() {
    let stuImport = $("#insertStuAtt").serializeJSON();
    $.ajax({
        type: "post",
        url: "../../File/insertStuAtt.do",
        data: JSON.stringify(stuImport),
        headers: {
            'Access-Token': localStorage.getItem("token")
        },
        contentType: "application/json;charset=utf-8",
        async: false,
        dataType: "json",
        success: function (data) {
            alert(data);
        },
        error: function (err) {
            console.log(err);
        }
    })
}

function replacePoint(str) {
    return str.replace(".", "\\.");
}

function getCozSuv() {
    let schId = window.parent.$("#schId").val().substring(1);
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/findCozSuv.do",
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: schId,
            async: true,
            dataType: "json",
            success: function (data) {
                let selector = window.parent.$("#suvWeek");
                for (let i = 0; i < data.length; i++) {
                    let week = data[i];
                    selector.append(`<option value="${week}">${week}</option>`);
                }
            },
            error: function (data) {
                console.log(data);
            }
        }
    );
}

function removeCozSuv() {
    let schId = window.parent.$("#schId").val().substring(1);
    let suvWeek = window.parent.$("#suvWeek").val();
    let paramStr = schId + "&" + suvWeek;
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/removeCozSuv.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            data: paramStr,
            async: true,
            dataType: "json",
            success: function (data) {
                if (data === true) {
                    alert("设置成功!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                } else {
                    alert("设置失败!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            },
            error: function (data) {
                console.log(data);
                alert("设置失败!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        }
    );
}

function setCozSuv() {
    let suvWeek = window.parent.$("#suvWeek").val();
    let schId = window.parent.$("#schId").val().substring(1);
    let paramStr = schId + "&" + suvWeek;
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/setCozSuv.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            data: paramStr,
            async: true,
            dataType: "json",
            success: function (data) {
                if (data === true) {
                    alert("设置成功!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                } else {
                    alert("设置失败!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            },
            error: function (data) {
                console.log(data);
                alert("设置失败!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        }
    );
}

function setCozSignIn() {
    let selector = window.parent.$("#signInChoice");
    let siWeek = window.parent.$("#siWeek").val();
    let schId = window.parent.$("#schId").val().substring(1);
    if (selector.data('method') === 'auto') {
        let suvMan = {
            schId: parseInt(schId),
            siWeek: parseInt(siWeek),
            siTime: [1970, 1, 1, 8, 0, 1],
            suvManAutoOpen: true
        };
        $.ajax(
            {
                type: "post",
                url: "../../Teacher/setCozSignIn.do",
                headers: {
                    'Access-Token': localStorage.getItem("token")
                },
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(suvMan),
                async: true,
                dataType: "json",
                success: function (data) {
                    if (data === true) {
                        alert("设置成功!");
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    } else {
                        alert("设置失败!");
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    }
                },
                error: function (data) {
                    console.log(data);
                    alert("设置失败!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            }
        );
    } else if (selector.data('method') === 'manual') {
        let dateTime = null;
        if (window.parent.$("#datetimeInput").val() === "") {
            return false;
        } else {
            dateTime = window.parent.$("#datetimeInput").val();
        }
        let suvMan = {
            schId: parseInt(schId),
            siWeek: parseInt(siWeek),
            siTime: turnDateTime(dateTime),
            suvManAutoOpen: false
        };
        $.ajax(
            {
                type: "post",
                url: "../../Teacher/setCozSignIn.do",
                headers: {
                    'Access-Token': localStorage.getItem("token")
                },
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(suvMan),
                async: true,
                dataType: "json",
                success: function (data) {
                    if (data === true) {
                        alert("设置成功!");
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    } else {
                        alert("设置失败!");
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    }
                },
                error: function (data) {
                    console.log(data);
                    alert("设置失败!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            }
        );
    } else if (selector.data('method') === 'both') {
        let dateTime = null;
        if (window.parent.$("#datetimeInput").val() === "") {
            return false;
        } else {
            dateTime = window.parent.$("#datetimeInput").val();
        }
        let suvMan = {
            schId: parseInt(schId),
            siWeek: parseInt(siWeek),
            siTime: turnDateTime(dateTime),
            suvManAutoOpen: true
        };
        $.ajax(
            {
                type: "post",
                url: "../../Teacher/setCozSignIn.do",
                headers: {
                    'Access-Token': localStorage.getItem("token")
                },
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(suvMan),
                async: true,
                dataType: "json",
                success: function (data) {
                    if (data === true) {
                        alert("设置成功!");
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    } else {
                        alert("设置失败!");
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    }
                },
                error: function (data) {
                    console.log(data);
                    alert("设置失败!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            }
        );
    } else {
        console.log("no method found!");
    }
}

function removeCozSignIn() {
    let schId = window.parent.$("#schId").val().substring(1);
    let siWeek = window.parent.$("#siWeek").val();
    let suvMan = {
        schId: schId,
        siWeek: siWeek
    };
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/removeCozSignIn.do",
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: JSON.stringify(suvMan),
            async: true,
            dataType: "json",
            success: function (data) {
                if (data === true) {
                    alert("设置成功!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                } else {
                    alert("设置失败!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            },
            error: function (data) {
                console.log(data);
                alert("设置失败!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        }
    );
}

function changeSignInMethod() {
    let selector = window.parent.$("#signInChoice");
    let method = selector.val();
    if (method === 'auto') {
        selector.removeData('method').data('method', 'auto');
        window.parent.$("#timePicker").hide();
    } else if (method === 'manual') {
        selector.removeData('method').data('method', 'manual');
        window.parent.$("#timePicker").show();
    } else if (method === 'both') {
        selector.removeData('method').data('method', 'both');
        window.parent.$("#timePicker").show();
    } else {
        console.log("error!");
    }
}

function getCozSignIn() {
    let schId = window.parent.$("#schId").val().substring(1);
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/getCozSignIn.do",
            contentType: "application/json;charset=utf-8",
            data: schId,
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                let selector = window.parent.$("#siWeek");
                for (let i = 0; i < data.length; i++) {
                    let week = data[i];
                    selector.append(`<option value="${week}">${week}</option>`);
                }
            },
            error: function (data) {
                console.log(data);
            }
        }
    );
}

function turnDateTime(dateTimeFormatted) {
    return [
        parseInt(dateTimeFormatted.substr(0, 4)),
        parseInt(dateTimeFormatted.substr(5, 2)),
        parseInt(dateTimeFormatted.substr(8, 2)),
        parseInt(dateTimeFormatted.substr(11, 2)),
        parseInt(dateTimeFormatted.substr(14, 2))
    ]
}

function approveLeave(siId) {
    let signInRes;
    for (let i = 0; i < signInResList.length; i++) {
        if (signInResList[i].siId === siId) {
            signInRes = signInResList[i];
        }
    }
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/approveLeave.do",
            data: JSON.stringify(signInRes),
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function () {
                let id = "#btn_group_" + siId;
                $(id).empty().text("已通过");
            },
            error: function () {
                alert("审批失败,请稍后再试!");
            }
        }
    );
}

function rejectLeave(siId) {
    let signInRes;
    for (let i = 0; i < signInResList.length; i++) {
        if (signInResList[i].siId === siId) {
            signInRes = signInResList[i];
        }
    }
    $.ajax(
        {
            type: "post",
            url: "../../Teacher/rejectLeave.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: JSON.stringify(signInRes),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function () {
                let id = "#btn_group_" + siId;
                $(id).empty().text("已驳回");
            },
            error: function () {
                alert("审批失败,请稍后再试!");
            }
        }
    );
}