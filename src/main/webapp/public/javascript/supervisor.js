"use strict";

let suvCourseList;
let hisSuvRecList;
let signInResList;
let toBeSupervisedList;
let modalMode = {
    "initAutoSignIn": {
        "title": "发起自动签到",
        "body": "<p>开启自动签到,请确认</p>",
        "action": initAutoSignIn
    },
    "initManSignIn": {
        "title": "发起人工签到",
        "body": "<form id=\"datetimePickerForm\" action=\"javascript:void(0)\">" +
        "<div class=\"form-group\">" +
        "<label for=\"datetimeInput\">请选择时间</label>" +
        "<input id=\"datetimeInput\" type=\"datetime-local\" class=\"form-control\" value=\"\">" +
        "<small id=\"datetimeHelp\" class=\"form-text text-muted\">发起时间至后十分钟有效" +
        "</small>" +
        "</div>" +
        "</form>",
        "action": initManSignIn
    },
    "closeAutoSignIn": {
        "title": "关闭自动签到",
        "body": "<p>关闭自动签到,请确认</p>",
        "action": closeAutoSignIn
    },
    "openAutoSignIn": {
        "title": "开启自动签到",
        "body": "<p>开启自动签到,请确认</p>",
        "aciton": openAutoSignIn
    },
    "openManSignIn": {
        "title": "开启人工签到",
        "body": "<form id=\"datetimePickerForm\" action=\"javascript:void(0)\">" +
        "<div class=\"form-group\">" +
        "<label for=\"datetimeInput\">请选择时间</label>" +
        "<input id=\"datetimeInput\" type=\"datetime-local\" class=\"form-control\" value=\"\">" +
        "<small id=\"datetimeHelp\" class=\"form-text text-muted\">发起时间至后十分钟有效" +
        "</small>" +
        "</div>" +
        "</form>",
        "action": openManSignIn
    },
    "closeManSignIn": {
        "title": "关闭人工签到",
        "body": "<p>关闭人工签到,请确认</p>",
        "action": closeManSignIn

    },
    "cancelSignIn": {
        "title": "取消签到",
        "body": "<p>取消签到, 请确认 </p>",
        "action": cancelSignIn
    },
    "suvLeave": {
        "title": "督导请假",
        "body": "<p>请假这次督导, 请确认! </p>",
        "action": suvLeave
    },
    "receiveSuv": {
        "title": "领取督导课程",
        "body": "<p>领取该督导课程, 请确认! </p>",
        "action": receiveSuvSch
    },
    "giveUpPower": {
        "title": "放弃督导权",
        "body": "<p>放弃该课程督导权, 请确认! </p>",
        "action": giveUpPower
    },
    "applyForTrans": {
        "title": "督导转接",
        "body": "<p>申请转接此次督导,请确认</p>" +
        "<input id='transUserId' class='form-control' name='transUserId' type='text'placeholder='请输入接受转接人学号'/>",
        "action": applyForTrans
    }
};

$(function () {
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        let button = window.parent.$(this).data("button");
        let suvId = button.data('suv_id');
        let mode = button.data('mode');
        window.parent.$(this).removeData("suv_id").data("suv_id", suvId);
        window.parent.$("#modalTitle").text(modalMode[mode].title);
        window.parent.$("#modalBody").empty().append(modalMode[mode].body);
        window.parent.$("#modalOk").unbind('click').click(modalMode[mode].action);
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        window.parent.$(this).removeData("suv_id").removeData("button");
        window.parent.$("#modalBody").empty();
    });
});

function getSuvCourse() {
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/showSuvCourses.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function (data) {
                suvCourseList = data;
                showSuvCourses();
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
            error: function (err) {
                console.log(err);
                suvCourseList = null;
            }
        }
    );

}

function showSuvCourses() {
    const statusMap = {
        true: "正常",
        false: "停课"
    };
    const leaveMap = {
        true: "已请假",
        false: "未请假"
    };
    if (suvCourseList != null) {
        for (let i = 0; i < suvCourseList.length; i++) {
            let node = suvCourseList[i];
            let cozStatus = true;
            if (node.schedule.cozSuspendList != null) {
                for (let j = 0; j < node.schedule.cozSuspendList.length; j++) {
                    if (node.schedule.cozSuspendList[j].susWeek == node.suvWeek) {
                        cozStatus = false;
                    }
                }
            }
            let popoverStr = `督导号: ${node.suvId}<br>督导日期: 第${toZhDigit(node.suvWeek)}周    星期${toZhDigit(node.schedule.schDay)}    第${toZhDigit(node.schedule.schTime)}节<br>课程信息: ${node.course.cozName}   ${node.course.teacher.userName}   ${node.schedule.location.locName}<br>签到设定: ${(node.suvMan == null) ?
                `未开启签到` :
                ("自动签到　" + ((node.suvMan.suvManAutoOpen === true) ? "开启" : "关闭") + "<br>　　　　&nbsp;&nbsp;人工签到时间  " + (isDeadTime(node.suvMan.siTime) ? "未开启" : node.suvMan.siTime))}<br>排课状态: ${statusMap[cozStatus]}<br>督导员请假: ${leaveMap[node.suvLeave]}`;
            let dropdownStr = "";
            if (node.suvMan == null) {
                dropdownStr = `${dropdownStr}<a class='dropdown-item' href='javascript:void(0)' id='initAutoSignIn_${node.suvId}'>开启自动签到</a>`;
                dropdownStr = `${dropdownStr}<a class='dropdown-item' href='javascript:void(0)' id='initManSignIn_${node.suvId}'>开启人工签到</a>`;
            } else {
                if (node.suvMan.suvManAutoOpen === true) {
                    dropdownStr = `${dropdownStr}<a class='dropdown-item' href='javascript:void(0)' id='closeAutoSignIn_${node.suvId}'>关闭自动签到</a>`;
                } else {
                    dropdownStr = `${dropdownStr}<a class='dropdown-item' href='javascript:void(0)' id='openAutoSignIn_${node.suvId}'>开启自动签到</a>`;
                }
                if (isDeadTime(node.suvMan.siTime)) {
                    dropdownStr = `${dropdownStr}<a class='dropdown-item' href='javascript:void(0)' id='openManSignIn_${node.suvId}'>开启人工签到</a>`;
                } else {
                    dropdownStr = `${dropdownStr}<a class='dropdown-item' href='javascript:void(0)' id='closeManSignIn_${node.suvId}'>关闭人工签到</a>`;
                }
                dropdownStr = `${dropdownStr}<a class='dropdown-item' href='javascript:void(0)' id='cancelSignIn_${node.suvId}'>取消签到</a>`;
            }
            let str = `<tr>` +
                `<th class='popover-tr popover-content' data-toggle="popover" data-placement="top" data-content="${popoverStr}" scope="row">${node.suvId}</th>` +
                `<td class='popover-tr'>${node.course.cozName}</td>` +
                `<td class='popover-tr'>第${toZhDigit(node.suvWeek)}周&nbsp;&nbsp;&nbsp;星期${toZhDigit(node.schedule.schDay)}&nbsp;&nbsp;&nbsp;第${toZhDigit(node.schedule.schTime)}节</td>` +
                `<td>${node.schedule.location.locName}</td>` +
                `<td>` +
                `<div id="dropdown_${node.suvId}">` +
                `<i class="fa fa-cog suv-coz-table-more" role="button" id="dropdownMenuButton_${node.suvId}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"></i>` +
                `<div class='dropdown-menu' aria-labelledby="dropdownMenuButton_${node.suvId}">` +
                dropdownStr +
                `<div class="dropdown-divider"></div>` +
                `<a id="suv_leave_${node.suvId}" class="dropdown-item text-danger font-weight-bold" href="javascript:void(0)">督导请假</a>` +
                `<a id="apply_for_trans_${node.suvId}" class="dropdown-item text-danger font-weight-bold" href="javascript:void(0)">督导转接</a>` +
                `<a id="giveUpPower_${node.suvId}" class="dropdown-item text-danger font-weight-bold" href="javascript:void(0)">放弃督导权</a>` +
                `</div></div></td></tr>`;
            $("#suv_sch").append(str);
            $("#initAutoSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "initAutoSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#initManSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "initManSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#openAutoSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "closeAutoSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#closeAutoSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "closeAutoSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#openManSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "openManSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#closeManSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "closeManSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#cancelSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "cancelSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#suv_leave_" + node.suvId).data("suv_id", node.suvId).data("mode", "suvLeave").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#giveUpPower_" + node.suvId).data("suv_id", node.suvId).data("mode", "giveUpPower").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#apply_for_trans_" + node.suvId).data("suv_id", node.suvId).data("mode", "applyForTrans").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
        }
    }
}

function getSuvTrans() {
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/getSuvTrans.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function (data) {
                showSuvTrans(data);
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
            error: function (err) {
                console.log(err);
            }
        }
    );
}

function showSuvTrans(suvTransList) {
    const statusMap = {
        true: "正常",
        false: "停课"
    };
    const leaveMap = {
        true: "已请假",
        false: "未请假"
    };
    if (suvTransList != null) {
        for (let i = 0; i < suvTransList.length; i++) {
            let node = suvTransList[i].suvSch;
            let cozStatus = true;
            if (node.schedule.cozSuspendList != null) {
                for (let j = 0; j < node.schedule.cozSuspendList.length; j++) {
                    if (node.schedule.cozSuspendList[j].susWeek == node.suvWeek) {
                        cozStatus = false;
                    }
                }
            }
            let popoverStr = `督导号: ${node.suvId}<br>督导日期: 第${toZhDigit(node.suvWeek)}周    星期${toZhDigit(node.schedule.schDay)}    第${toZhDigit(node.schedule.schTime)}节<br>课程信息: ${node.course.cozName}   ${node.course.teacher.userName}   ${node.schedule.location.locName}<br>签到设定: ${(node.suvMan == null) ?
                `未开启签到` :
                ("自动签到　" + ((node.suvMan.suvManAutoOpen === true) ? "开启" : "关闭") + "<br>　　　　&nbsp;&nbsp;人工签到时间  " + (isDeadTime(node.suvMan.siTime) ? "未开启" : node.suvMan.siTime))}<br>排课状态: ${statusMap[cozStatus]}<br>督导员请假: ${leaveMap[node.suvLeave]}`;
            let str = `<tr>` +
                `<th class='popover-tr popover-content' data-toggle="popover" data-placement="top" data-content="${popoverStr}" scope="row">${node.suvId}</th>` +
                `<td class='popover-tr'>${node.course.cozName}</td>` +
                `<td class='popover-tr'>第${toZhDigit(node.suvWeek)}周&nbsp;&nbsp;&nbsp;星期${toZhDigit(node.schedule.schDay)}&nbsp;&nbsp;&nbsp;第${toZhDigit(node.schedule.schTime)}节</td>` +
                `<td>${node.schedule.location.locName}</td>` +
                `<td class="btn-group" ><button id="trans_accept_${suvTransList[i].sutrId}" class="btn btn-sm btn-success">接受</button><button id="trans_refuse_${suvTransList[i].sutrId}" class="btn btn-sm btn-danger">拒绝</button></td></tr>`;
            $("#suv_sch").append(str);
            $("#trans_accept_" + suvTransList[i].sutrId).data("sutr_id", suvTransList[i].sutrId).click(function () {
                let sutrId = parseInt($(this).data("sutr_id").toString());
                let suvTrans = null;
                for (let i = 0; i < suvTransList.length; i++) {
                    if (sutrId === suvTransList[i].sutrId) {
                        suvTrans = suvTransList[i];
                    }
                }
                $.ajax(
                    {
                        type: "post",
                        url: "../../Supervisor/acceptSuvTrans.do",
                        data: JSON.stringify(suvTrans),
                        headers: {
                            'Access-Token': localStorage.getItem("token")
                        },
                        contentType: "application/json;charset=utf-8",
                        async: true,
                        dataType: "json",
                        success: function (data) {
                            if (data === true) {
                                alert("操作成功");
                                location.href = location.href;
                                window.parent.$('#indexModal').modal('hide');
                            } else {
                                alert("操作失败")
                                location.href = location.href;
                                window.parent.$('#indexModal').modal('hide');
                            }
                        },
                        error: function (data) {
                            console.log(data);
                            alert("提交失败,请稍后再试!");
                            location.href = location.href;
                            window.parent.$('#indexModal').modal('hide');
                        }
                    }
                );
            });
            $("#trans_refuse_" + suvTransList[i].sutrId).data("sutr_id", suvTransList[i].sutrId).click(function () {
                let sutrId = parseInt($(this).data("sutr_id").toString());
                let suvTrans = null;
                for (let i = 0; i < suvTransList.length; i++) {
                    if (sutrId === suvTransList[i].sutrId) {
                        suvTrans = suvTransList[i];
                    }
                }
                $.ajax(
                    {
                        type: "post",
                        url: "../../Supervisor/refuseSuvTrans.do",
                        headers: {
                            'Access-Token': localStorage.getItem("token")
                        },
                        data: JSON.stringify(suvTrans),
                        contentType: "application/json;charset=utf-8",
                        async: true,
                        dataType: "json",
                        success: function (data) {
                            if (data === true) {
                                alert("操作成功");
                                location.href = location.href;
                                window.parent.$('#indexModal').modal('hide');
                            } else {
                                alert("操作失败")
                                location.href = location.href;
                                window.parent.$('#indexModal').modal('hide');
                            }
                        },
                        error: function (data) {
                            console.log(data);
                            alert("提交失败,请稍后再试!");
                            location.href = location.href;
                            window.parent.$('#indexModal').modal('hide');
                        }
                    }
                );
            });
            $("#initAutoSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "initAutoSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#initManSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "initManSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#openAutoSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "closeAutoSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#closeAutoSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "closeAutoSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#openManSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "openManSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#closeManSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "closeManSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#cancelSignIn_" + node.suvId).data("suv_id", node.suvId).data("mode", "cancelSignIn").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#suv_leave_" + node.suvId).data("suv_id", node.suvId).data("mode", "suvLeave").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#giveUpPower_" + node.suvId).data("suv_id", node.suvId).data("mode", "giveUpPower").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
            $("#apply_for_trans_" + node.suvId).data("suv_id", node.suvId).data("mode", "applyForTrans").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
        }
    }
}

function dropdownSuvSch(suvId) {
    let str = "#dropdownMenuButton_" + suvId;
    $(str).dropdown("toggle");
}

function findHisSuvRecRes() {
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/findHisSuvRecRes.do",
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                hisSuvRecList = data;
                showHisSuvRecRes();
            },
            error: function (err) {
                console.log(err);
                hisSuvRecList = null;
            }
        }
    )
}

function showHisSuvRecRes() {
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/showSuvCourses.do",
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                suvCourseList = data;
                let creditAbsScore = 0;
                for (let i = 0; i < suvCourseList.length; i++) {
                    if (suvCourseList[i].suvLeave === true) {
                        creditAbsScore++;
                    }
                }
                $("#count_sco").text(100 - creditAbsScore);
                $("#count_abs").text(creditAbsScore);
                $("#count_rec").text(hisSuvRecList.length);
            },
            error: function (err) {
                console.log(err);
                suvCourseList = null;
            }
        }
    );
    for (let i = 0; i < hisSuvRecList.length; i++) {
        let oneCozAndSch = hisSuvRecList[i].oneCozAndSch;
        let popoverStr = "督导号: " +
            hisSuvRecList[i].suvId +
            "<br>课程信息: " +
            oneCozAndSch.course.cozName +
            "&nbsp;&nbsp;&nbsp;" +
            oneCozAndSch.course.teacher.userName +
            "&nbsp;&nbsp;&nbsp;" +
            oneCozAndSch.schedule.location.locName +
            "<br>督导时间: " +
            toZhDigit(hisSuvRecList[i].suvWeek) +
            "周&nbsp;&nbsp;星期" +
            toZhDigit(oneCozAndSch.schedule.schDay) +
            "&nbsp;&nbsp;第" +
            oneCozAndSch.schedule.schTime +
            "节<br>督导备注: " +
            hisSuvRecList[i].suvRecord.suvRecInfo;
        let str = "<tr class=\"item his-suv-rec-res\" data-toggle=\"collapse\" data-parent=\"#tBody\" href=\"#exampleAccordion" + i + 1 + "\" aria-expanded=\"true\" aria-controls=\"exampleAccordion" + i + 1 + "\" ><th scope=\"row\">" +
            hisSuvRecList[i].suvId +
            "</th><td>" +
            oneCozAndSch.course.cozName +
            "&nbsp;&nbsp;&nbsp;" +
            oneCozAndSch.course.teacher.userName +
            "</td><td>第" +
            toZhDigit(hisSuvRecList[i].suvWeek) +
            "周&nbsp;&nbsp;&nbsp;星期" +
            toZhDigit(oneCozAndSch.schedule.schDay) +
            "&nbsp;&nbsp;&nbsp;第" +
            oneCozAndSch.schedule.schTime +
            "节</td>" +
            "<td>" +
            hisSuvRecList[i].suvRecord.suvRecName +
            "</td><td>" +
            hisSuvRecList[i].suvRecord.suvRecNum +
            "</td><td>" +
            hisSuvRecList[i].suvRecord.suvRecBadNum +
            "</td></tr>" +
            "<tr id=\"exampleAccordion" + i + 1 + "\" class=\"collapse\" role=\"tabpanel\" style=\"\"><td class=\"text-left\" colspan=\"6\">" +
            popoverStr +
            "</td></tr>";
        $("#suv_rec").append(str);
    }
}

function getLeaves() {
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/getLeaves.do",
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
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
        let tr = `<tr><th class='popover-tr popover-content align-middle' data-toggle='popover' data-placement='top' data-content='${popoverStr}' scope='row'>` +
            `${signInRes.siId}</th><td>${oneCozAndSch.course.cozName}<br>${oneCozAndSch.course.teacher.userName}</td>` +
            `<td>第${toZhDigit(signInRes.siWeek)}周&nbsp;&nbsp;星期${toZhDigit(oneCozAndSch.schedule.schDay)}<br>第${oneCozAndSch.schedule.schTime}节</td>` +
            `<td>${signInRes.student.userId}<br>${signInRes.student.userName}</td>` +
            `<td>${signInRes.siTime[0]}-${signInRes.siTime[1] < 10 ? "0" + signInRes.siTime[1] : signInRes.siTime[1]}-${signInRes.siTime[2] < 10 ? "0" + signInRes.siTime[2] : signInRes.siTime[2]}` +
            `<br>${signInRes.siTime[3] < 10 ? "0" + signInRes.siTime[3] : signInRes.siTime[3]}:${signInRes.siTime[4] < 10 ? "0" + signInRes.siTime[4] : signInRes.siTime[4]}:${signInRes.siTime[5] < 10 ? "0" + signInRes.siTime[5] : signInRes.siTime[5]}</td>` +
            `<td class='btn-group pt-3' id='btn_group_${signInRes.siId}'><button class='btn btn-success btn-sm' id='leave_btn_approve_${signInRes.siId}' onclick='approveLeave(${signInRes.siId})'>通过</button>` +
            `<button class='btn btn-danger btn-sm' id='leave_btn_reject_${signInRes.siId}' onclick='rejectLeave(${signInRes.siId})'>驳回</button></td></tr>`;
        $("#suv_leave").append(tr);
        let id = "#btn_group_" + signInRes.siId;
        if (signInRes.siApprove == 1) {
            $(id).empty().text("已通过");
        } else if (signInRes.siApprove == 2) {
            $(id).empty().text("已驳回");
        }
    }
}

function approveLeave(siId) {
    let signInRes;
    for (let i = 0; i < signInResList.length; i++) {
        if (signInResList[i].siId == siId) {
            signInRes = signInResList[i];
        }
    }
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/approveLeave.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: JSON.stringify(signInRes),
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
        if (signInResList[i].siId == siId) {
            signInRes = signInResList[i];
        }
    }
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/rejectLeave.do",
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

function initAutoSignIn() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = null;
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        let suvMan = {
            "schId": suvSch.schedule.schId,
            "siWeek": suvSch.suvWeek
        };
        $.ajax({
            url: "../../Supervisor/initAutoSignIn.do",
            type: "post",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: JSON.stringify(suvMan),
            contentType: "application/json;charset=utf-8",
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
                alert("未知错误, 设置失败!");
                console.log(data);
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        });
    }
}

function initManSignIn() {
    let dateTime = null;
    let suvSch = null;
    if (window.parent.$("#datetimeInput").val() == "") {
        return false;
    } else {
        dateTime = window.parent.$("#datetimeInput").val();
    }
    let suvId = window.parent.$('#indexModal').data("suv_id");
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        let suvMan = {
            "schId": suvSch.schedule.schId,
            "siWeek": suvSch.suvWeek,
            "siTime": turnDateTime(dateTime)
        };
        $.ajax({
            url: "../../Supervisor/initManSignIn.do",
            type: "post",
            data: JSON.stringify(suvMan),
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
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
                alert("未知错误, 设置失败!");
                console.log(data);
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        });
    }
}

function closeAutoSignIn() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = null;
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        let suvMan = {
            "schId": suvSch.schedule.schId,
            "siWeek": suvSch.suvWeek
        };
        $.ajax({
            url: "../../Supervisor/closeAutoSignIn.do",
            type: "post",
            data: JSON.stringify(suvMan),
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
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
                alert("未知错误, 设置失败!");
                console.log(data);
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        });
    }
}

function openAutoSignIn() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = null;
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        let suvMan = {
            "schId": suvSch.schedule.schId,
            "siWeek": suvSch.suvWeek
        };
        $.ajax({
            url: "../../Supervisor/openAutoSignIn.do",
            type: "post",
            data: JSON.stringify(suvMan),
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
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
                alert("未知错误, 设置失败!");
                console.log(data);
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        });
    }
}

function openManSignIn() {
    let dateTime = null;
    let suvSch = null;
    if (window.parent.$("#datetimeInput").val() == "") {
        return false;
    } else {
        dateTime = window.parent.$("#datetimeInput").val();
    }
    let suvId = window.parent.$('#indexModal').data("suv_id");
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        let suvMan = {
            "schId": suvSch.schedule.schId,
            "siWeek": suvSch.suvWeek,
            "siTime": turnDateTime(dateTime)
        };
        $.ajax({
            url: "../../Supervisor/openManSignIn.do",
            type: "post",
            data: JSON.stringify(suvMan),
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
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
                alert("未知错误, 设置失败!");
                console.log(data);
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        });
    }
}

function closeManSignIn() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = null;
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        let suvMan = {
            "schId": suvSch.schedule.schId,
            "siWeek": suvSch.suvWeek
        };
        $.ajax({
            url: "../../Supervisor/closeManSignIn.do",
            type: "post",
            data: JSON.stringify(suvMan),
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
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
                alert("未知错误, 设置失败!");
                console.log(data);
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        });
    }
}

function cancelSignIn() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = null;
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        let suvMan = {
            "schId": suvSch.schedule.schId,
            "siWeek": suvSch.suvWeek
        };
        $.ajax({
            url: "../../Supervisor/cancelSignIn.do",
            type: "post",
            data: JSON.stringify(suvMan),
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
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
                alert("未知错误, 设置失败!");
                console.log(data);
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        });
    }
}

function suvLeave() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = null;
    for (let i = 0; i < suvCourseList.length; i++) {
        if (suvCourseList[i].suvId == suvId) {
            suvSch = suvCourseList[i];
        }
    }
    if (suvSch != null) {
        $.ajax(
            {
                type: "post",
                url: "../../Supervisor/suvLeave.do",
                data: JSON.stringify(suvSch),
                headers: {
                    'Access-Token': localStorage.getItem("token")
                },
                contentType: "application/json;charset=utf-8",
                async: true,
                dataType: "json",
                success: function (data) {
                    if (data === true) {
                        alert("请假成功");
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    } else {
                        alert("请假失败")
                        location.href = location.href;
                        window.parent.$('#indexModal').modal('hide');
                    }
                },
                error: function (data) {
                    console.log(data);
                    alert("提交失败,请稍后再试!");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            }
        );
    } else {
        alert("未知错误!")
        location.href = location.href;
        window.parent.$('#indexModal').modal('hide');
    }
}

function isDeadTime(dateTime) {
    let deadTime = JSON.stringify([1970, 1, 1, 8, 0, 1]);
    return JSON.stringify(dateTime) == deadTime;
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

function getToBeSupervised() {
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/findToBeSupervised.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function (data) {
                toBeSupervisedList = data;
                showToBeSupervised();
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
            error: function (err) {
                console.log(err);
                toBeSupervisedList = null;
            }
        }
    );
}

function showToBeSupervised() {
    let statusMap = {
        true: "正常",
        false: "停课"
    };
    if (toBeSupervisedList != null) {
        for (let i = 0; i < toBeSupervisedList.length; i++) {
            let node = toBeSupervisedList[i];
            let cozStatus = true;
            if (node.schedule.cozSuspendList != null) {
                for (let j = 0; j < node.schedule.cozSuspendList.length; j++) {
                    if (node.schedule.cozSuspendList[j].susWeek == node.suvWeek) {
                        cozStatus = false;
                    }
                }
            }
            let popoverStr = "督导号: " +
                node.suvId +
                "<br>督导日期: 第" +
                toZhDigit(node.suvWeek) +
                "周    星期" +
                toZhDigit(node.schedule.schDay) +
                "    第" +
                toZhDigit(node.schedule.schTime) +
                "节<br>课程信息: " +
                node.course.cozName +
                "   " +
                node.course.teacher.userName +
                "   " +
                node.schedule.location.locName +
                "<br>签到设定: " +
                ((node.suvMan == null) ?
                        "未开启签到" :
                        ("自动签到　" + ((node.suvMan.suvManAutoOpen === true) ? "开启" : "关闭") + "<br>　　　　&nbsp;&nbsp;人工签到时间  " + (isDeadTime(node.suvMan.siTime) ? "未开启" : node.suvMan.siTime))
                ) +
                "<br>排课状态: " +
                statusMap[cozStatus];
            let str = "<tr><th class='popover-tr popover-content' data-toggle=\"popover\" data-placement=\"top\" data-content=\"" +
                popoverStr +
                "\" scope=\"row\">" +
                node.suvId +
                "</th><td class='popover-tr'>" +
                node.course.cozName +
                "</td><td class='popover-tr'>第" +
                toZhDigit(node.suvWeek) +
                "周&nbsp;&nbsp;&nbsp;星期" +
                toZhDigit(node.schedule.schDay) +
                "&nbsp;&nbsp;&nbsp;第" +
                toZhDigit(node.schedule.schTime) +
                "节</td>" +
                "<td>" +
                node.schedule.location.locName +
                "</td><td><button class='btn btn-sm btn-light pt-0 pb-0' id='receive_" + node.suvId + "'>领取</button></td></tr>";
            $("#suv_sch").append(str);
            $("#receive_" + node.suvId).data("suv_id", node.suvId).data("mode", "receiveSuv").click(function () {
                window.parent.$('#indexModal').data("button", $(this)).modal('show');
            });
        }
    }
}

function receiveSuvSch() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = {
        "suvId": suvId,
    };
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/receiveSuvSch.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: JSON.stringify(suvSch),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function (data) {
                if (data === true) {
                    alert("领取成功");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                } else {
                    alert("领取失败")
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            },
            error: function (data) {
                console.log(data);
                alert("提交失败,请稍后再试!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        }
    );
}

function giveUpPower() {
    let suvId = window.parent.$('#indexModal').data("suv_id");
    let suvSch = {
        "suvId": suvId,
        "student": JSON.parse(localStorage.getItem("user"))
    };
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/giveUpPower.do",
            data: JSON.stringify(suvSch),
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                if (data === true) {
                    alert("操作成功");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                } else {
                    alert("操作失败")
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            },
            error: function (data) {
                console.log(data);
                alert("提交失败,请稍后再试!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        }
    );
}

function applyForTrans() {
    let suvTrans = {
        suvSch: {
            "suvId": window.parent.$('#indexModal').data("suv_id"),
            "student": JSON.parse(localStorage.getItem("user"))
        },
        userId: window.parent.$("#transUserId").val()
    };
    $.ajax(
        {
            type: "post",
            url: "../../Supervisor/applyForTrans.do",
            data: JSON.stringify(suvTrans),
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                if (data === true) {
                    alert("操作成功");
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                } else {
                    alert("操作失败")
                    location.href = location.href;
                    window.parent.$('#indexModal').modal('hide');
                }
            },
            error: function (data) {
                console.log(data);
                alert("提交失败,请稍后再试!");
                location.href = location.href;
                window.parent.$('#indexModal').modal('hide');
            }
        }
    );
}