let modal = {
    "title": "课程信息",
    "body": "                    <nav class=\"nav nav-tabs nav-fill\">\n" +
    "                        <a class=\"nav-item nav-link active\" href=\"javascript:void(0)\" id=\"stu_info_a\">基本信息</a>\n" +
    "                        <a class=\"nav-item nav-link\" href=\"javascript:void(0)\" id=\"hiz_sign_in_a\"督导详情</a>\n" +
    "                        <a class=\"nav-item nav-link\" href=\"javascript:void(0)\" id=\"his_leave_a\">查看课表</a>\n" +
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
        "    <tbody id=\"adm_coz_stu\">\n" +
        "    </tbody>\n" +
        "</table>",
        "action": function () {
            window.parent.$('#indexModal').modal('hide');
        }
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
    },
    "approveLeave": {
        "title": "取消签到",
        "body": "<div class=\"container\">\n" +
        "    <div class=\"row\">\n" +
        "        <div class=\"col-12\">\n" +
        "            <table class=\"table table-sm table-striped table-hover suv-table text-center\">\n" +
        "                <thead>\n" +
        "                <tr>\n" +
        "                    <th>签到号</th>\n" +
        "                    <th>课程信息</th>\n" +
        "                    <th>上课时间</th>\n" +
        "                    <th>请假学生</th>\n" +
        "                    <th>请假时间</th>\n" +
        "                    <th>审批</th>\n" +
        "                </tr>\n" +
        "                </thead>\n" +
        "                <tbody id=\"suv_leave\"></tbody>\n" +
        "            </table>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>",
        "action": function () {
            window.parent.$('#indexModal').modal('hide');
        }
    }
};

let searchVal;
let searchGradeVal;
let searchDepartVal;
let oneStuSchedule;
let standardCourseList;
let last;

let statisticsGradeVal;
let statisticsDepartVal;
let statisticsCozNameVale;
let statisticsUserId;
let statisticsUserName;
let statisticsWeekVal;

function absStatisticsReady() {
    let statistics_grade = $("#statistics_grade");
    let statistics_depart = $("#statistics_depart");
    let statistics_coz_name = $("#statistics_coz_name");
    let statistics_user_id = $('#statistics_user_id');
    let statistics_user_name = $('#statistics_user_name');
    let statistics_week = $('#statistics_week');
    $("#search_btn").click(function () {
        statisticsGradeVal = statistics_grade.val() === "" ? null : statistics_grade.val();
        statisticsDepartVal = statistics_depart.val() === "" ? null : statistics_depart.val();
        statisticsCozNameVale = statistics_coz_name.val() === "" ? null : statistics_coz_name.val();
        statisticsUserId = statistics_user_id.val() === "" ? null : statistics_user_id.val();
        statisticsUserName = statistics_user_name.val() === "" ? null : statistics_user_name.val();
        statisticsWeekVal = statistics_week.val() === "" ? null : statistics_week.val();
        absStatisticsShow();
    });
}

function absStatisticsShow(page = 1) {
    $.ajax({
            type: "get",
            url: "../../Administrator/selectAbcStatistics.do",
            async: true,
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: {
                page: page - 1,
                cozName: statisticsCozNameVale,
                cozDepart: statisticsDepartVal,
                cozGrade: statisticsGradeVal == null ? null : parseInt(statisticsGradeVal),
                week: statisticsWeekVal == null ? null : parseInt(statisticsWeekVal),
                userId: statisticsUserId,
                userName: statisticsUserName
            },
            contentType: "application/json;charset=utf-8",
            dataType: "json",
            success: function (data) {
                let pagination = $("#pagination");
                pagination.whjPaging({
                    //可选，css设置，可设置值：css-1，css-2，css-3，css-4，css-5，默认css-1，可自定义样式
                    css: 'css-2',
                    //可选，总页数
                    totalPage: Math.ceil(data.totalNum / 10),
                    //可选，展示页码数量，默认5个页码数量
                    showPageNum: 10,
                    //可选，首页按钮展示文本，默认显示文本为首页
                    firstPage: '首页',
                    //可选，上一页按钮展示文本，默认显示文本为上一页
                    previousPage: '上一页',
                    //可选，下一页按钮展示文本，默认显示文本为下一页
                    nextPage: '下一页',
                    //可选，尾页按钮展示文本，默认显示文本为尾页
                    lastPage: '尾页',
                    //可选，跳至展示文本，默认显示文本为跳至
                    skip: '跳至',
                    //可选，确认按钮展示文本，默认显示文本为确认
                    confirm: '确认',
                    //可选，刷新按钮展示文本，默认显示文本为刷新
                    refresh: '刷新',
                    //可选，共{}页展示文本，默认显示文本为共{}页，其中{}会在js具体转化为数字
                    totalPageText: '共{}页',
                    //可选，是否展示每页N条下拉框，默认true
                    isShowPageSizeOpt: false,
                    //可选，是否展示首页与尾页，默认true
                    isShowFL: true,
                    //可选，是否展示跳到指定页数，默认true
                    isShowSkip: true,
                    //可选，是否展示刷新，默认true
                    isShowRefresh: true,
                    //可选，是否展示共{}页，默认true
                    isShowTotalPage: true,
                    //可选，是否需要重新设置当前页码及总页数，默认false，如果设为true，那么在请求服务器返回数据时，需要调用setPage方法
                    isResetPage: false,
                    //必选，回掉函数，返回参数：第一个参数为页码，第二个参数为每页显示N条
                    callBack: function (currPage) {
                        absStatisticsShow(currPage);
                    }
                });
                pagination.whjPaging("setPage", page, Math.ceil(data.totalNum / 10));
                let adm_coz_table = $('#adm_coz_table');
                adm_coz_table.empty();
                for (let i = 0; i < data.list.length; i++) {
                    let schAbsRec = data.list[i];
                    for (let j = 0; j < schAbsRec.studentList.length; j++) {
                        let student = schAbsRec.studentList[j];
                        adm_coz_table.append(`<tr><td>${schAbsRec.sarWeek}</td><td>${student.userId}</td><td>${student.userName}</td><td>${schAbsRec.course.cozName}</td></tr>`);
                    }
                }
                console.log(data);
            },
            error: function (data) {
                console.log(data);
            }
        }
    );
}

function admCozTableReady() {
    let id_search_course_input = $("#search_course_input");
    let search_course_grade_input = $("#search_course_grade_input");
    let search_course_depart_input = $("#search_course_depart_input");
    $("#search_btn").click(function () {
        searchVal = id_search_course_input.val() === "" ? null : id_search_course_input.val();
        searchGradeVal = search_course_grade_input.val() === "" ? null : search_course_grade_input.val();
        searchDepartVal = search_course_depart_input.val() === "" ? null : search_course_depart_input.val();
        changeOrder(null, null, searchVal, searchDepartVal, searchGradeVal);
    });
    $("#defaultOrder").parent().click(function () {
        $(".order-icon").empty();
        changeOrder();
    });
    $("#cozNameOrderDropDownMenuLink").mouseover(function () {
        $("#cozNameOrderDropDownMenu").show();
    });
    $("#cozNameOrderDropDownMenu").mouseover(function () {
        $("#cozNameOrderDropDownMenu").show();
    }).mouseout(function () {
        $("#cozNameOrderDropDownMenu").hide();
    });
    $("#attendOrderDropDownMenuLink").mouseover(function () {
        $("#attendOrderDropDownMenu").show();
    });
    $("#attendOrderDropDownMenu").mouseover(function () {
        $("#attendOrderDropDownMenu").show();
    }).mouseout(function () {
        $("#attendOrderDropDownMenu").hide();
    });
    $("#numberOrderDropDownMenuLink").mouseover(function () {
        $("#numberOrderDropDownMenu").show();
    });
    $("#numberOrderDropDownMenu").mouseover(function () {
        $("#numberOrderDropDownMenu").show();
    }).mouseout(function () {
        $("#numberOrderDropDownMenu").hide();
    });
    $("#cozNameUpOrder").click(function () {
        $(".order-icon").empty();
        $("#cozNameOrderDropDownMenuLink").find("span").html("<i class=\"fa fa-angle-double-up\" aria-hidden=\"true\"></i>");
        changeOrder("cozName");
    });
    $("#cozNameDownOrder").click(function () {
        $(".order-icon").empty();
        $("#cozNameOrderDropDownMenuLink").find("span").html("<i class=\"fa fa-angle-double-down\" aria-hidden=\"true\"></i>");
        changeOrder("cozName", false);
    });
    $("#attendUpOrder").click(function () {
        $(".order-icon").empty();
        $("#attendOrderDropDownMenuLink").find("span").html("<i class=\"fa fa-angle-double-up\" aria-hidden=\"true\"></i>");
        changeOrder("cozAttRate");
    });
    $("#attendDownOrder").click(function () {
        $(".order-icon").empty();
        $("#attendOrderDropDownMenuLink").find("span").html("<i class=\"fa fa-angle-double-up\" aria-hidden=\"true\"></i>");
        changeOrder("cozAttRate", false);
    });
    $("#numberUpOrder").click(function () {
        $(".order-icon").empty();
        $("#numberOrderDropDownMenuLink").find("span").html("<i class=\"fa fa-angle-double-up\" aria-hidden=\"true\"></i>");
        changeOrder("cozSize");
    });
    $("#numberDownOrder").click(function () {
        $(".order-icon").empty();
        $("#numberOrderDropDownMenuLink").find("span").html("<i class=\"fa fa-angle-double-up\" aria-hidden=\"true\"></i>");
        changeOrder("cozSize", false);
    });
    $("#cozDetailBack").click(function () {
        $(".adm-body").css("overflow-y", "hidden");
        $(".container").fadeOut(500).filter(".adm-coz-table-container").delay(500).fadeIn(500, function () {
            $(".adm-body").css("overflow-y", "auto");
        });
    });
    getAdmCozTable();
}

function changeOrder(sortStr = null, isAsc = true, cozName = searchVal, cozDepart = searchDepartVal, cozGrade = searchGradeVal) {
    $.ajax({
            type: "get",
            url: "../../Administrator/getAllCourseNumber.do",
            async: true,
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: {
                cozName: cozName,
                cozDepart: cozDepart,
                cozGrade: cozGrade == null ? null : parseInt(cozGrade)
            },
            contentType: "application/json;charset=utf-8",
            dataType: "json",
            success: function (data) {
                $("#pagination").empty().removeData().whjPaging({
                    //可选，css设置，可设置值：css-1，css-2，css-3，css-4，css-5，默认css-1，可自定义样式
                    css: 'css-2',
                    //可选，总页数
                    totalPage: data.pageNum,
                    //可选，展示页码数量，默认5个页码数量
                    showPageNum: 8,
                    //可选，首页按钮展示文本，默认显示文本为首页
                    firstPage: '首页',
                    //可选，上一页按钮展示文本，默认显示文本为上一页
                    previousPage: '上一页',
                    //可选，下一页按钮展示文本，默认显示文本为下一页
                    nextPage: '下一页',
                    //可选，尾页按钮展示文本，默认显示文本为尾页
                    lastPage: '尾页',
                    //可选，跳至展示文本，默认显示文本为跳至
                    skip: '跳至',
                    //可选，确认按钮展示文本，默认显示文本为确认
                    confirm: '确认',
                    //可选，刷新按钮展示文本，默认显示文本为刷新
                    refresh: '刷新',
                    //可选，共{}页展示文本，默认显示文本为共{}页，其中{}会在js具体转化为数字
                    totalPageText: '共{}页',
                    //可选，是否展示每页N条下拉框，默认true
                    isShowPageSizeOpt: false,
                    //可选，是否展示首页与尾页，默认true
                    isShowFL: true,
                    //可选，是否展示跳到指定页数，默认true
                    isShowSkip: true,
                    //可选，是否展示刷新，默认true
                    isShowRefresh: true,
                    //可选，是否展示共{}页，默认true
                    isShowTotalPage: true,
                    //可选，是否需要重新设置当前页码及总页数，默认false，如果设为true，那么在请求服务器返回数据时，需要调用setPage方法
                    isResetPage: false,
                    //必选，回掉函数，返回参数：第一个参数为页码，第二个参数为每页显示N条
                    callBack: function (currPage, pageSize) {
                        $.ajax(
                            {
                                type: "get",
                                url: "../../Administrator/showAllCourses.do",
                                async: true,
                                headers: {
                                    'Access-Token': localStorage.getItem("token")
                                },
                                data: {
                                    page: currPage,
                                    sortStr: sortStr,
                                    isAsc: isAsc,
                                    cozDepart: searchDepartVal,
                                    cozGrade: searchGradeVal == null ? null : parseInt(searchGradeVal),
                                    cozName: searchVal,
                                },
                                contentType: "application/json;charset=utf-8",
                                dataType: "json",
                                success: function (data) {
                                    standardCourseList = data;
                                    showAdmCozTable(data);
                                },
                                error: function (data) {
                                    console.log(data);
                                }
                            }
                        );
                    }
                });
                $("#pagination").whjPaging("setPage", 1, data.pageNum);
                $.ajax(
                    {
                        type: "get",
                        url: "../../Administrator/showAllCourses.do",
                        async: true,
                        headers: {
                            'Access-Token': localStorage.getItem("token")
                        },
                        data: {
                            page: 1,
                            sortStr: sortStr,
                            isAsc: isAsc,
                            cozDepart: searchDepartVal,
                            cozGrade: searchGradeVal == null ? null : parseInt(searchGradeVal),
                            cozName: cozName
                        },
                        contentType: "application/json;charset=utf-8",
                        dataType: "json",
                        success: function (data) {
                            standardCourseList = data;
                            showAdmCozTable(data);
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

function findSuv() {
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/findSuv.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                showSupervisor(data);
            },
            error: function (data) {
                console.log(data);
            }
        }
    );
}

function showSupervisor(studentList) {
    for (let i = 0; i < studentList.length; i++) {
        let student = studentList[i];
        let str = "<tr><td>" + student.userId + "</td><td>" + student.userName + "</td><td>" +
            "<div id='dropdown_" + student.userId + "'><i class='fa fa-cog adm-suv-more' role='button' id='dropdownMenuButton_" + student.userId + "' data-toggle='dropdown' aria-haspopup='true' aria-expanded='false'></i>" +
            "<div class='dropdown-menu' aria-labelledby='dropdownMenuButton_" + student.userId + "'>" +
            "<a id='basicInfo_" + student.userId + "' class='dropdown-item' href='javascript:void(0)'>基本信息</a>" +
            "<a id='suvInfo_" + student.userId + "' class='dropdown-item' href='javascript:void(0)'>督导课程</a>" +
            "<a id='cozInfo_" + student.userId + "' class='dropdown-item' href='javascript:void(0)'>查看课表</a>" +
            "</div></div></td></tr>";
        $("#adm_suv_leave").append(str);
        addModalForSuv(student);
    }
}

function addModalForSuv(student) {
    let userId = student.userId;
    $("#basicInfo_" + userId).click(function () {
        let modal = {
            "title": "督导员基本信息",
            "body": "<dl class='row'>" +
            "<dt class='col-3'>学号</dt><dd class='col-9' id='stuId'></dd>" +
            "<dt class='col-3'>姓名</dt><dd class='col-9' id='stuName'></dd>" +
            "<dt class='col-3'>督导次数</dt><dd class='col-9' id='stuSuvTime'></dd>" +
            "<dt class='col-3'>信用分数</dt><dd class='col-9' id='creditScore'></dd>" +
            "</dl>",
            "action": function () {
                window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').modal('hide');
            }
        };
        window.parent.$("#modalTitle").text(modal.title);
        window.parent.$("#modalBody").empty().append(modal.body);
        window.parent.$("#modalOk").unbind('click').click(modal.action);
        window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').on('show.bs.modal', basicInfo(student)).on('hidden.bs.modal', function () {
            window.parent.$("#modalBody").empty();
            window.parent.$("#modalBody").empty();
            window.parent.$("#modalOk").unbind('click');
            window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal');
        }).modal('show');
    });
    $("#suvInfo_" + userId).click(function () {
        let modal = {
            "title": "查看督导员督导课程",
            "body": "<table class='table table-hover table-sm text-center'>" +
            "<thead>\n" +
            "<tr>\n" +
            "<th>督导号</th>\n" +
            "<th>课程</th>\n" +
            "<th>日期</th>\n" +
            "<th>地点</th>\n" +
            "</tr>\n" +
            "</thead>\n" +
            "<tbody id=\"suv_sch\">\n" +
            "</tbody>" +
            "</table>",
            "action": function () {
                window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').modal('hide');
            }
        };
        window.parent.$("#modalTitle").text(modal.title);
        window.parent.$("#modalBody").empty().append(modal.body);
        window.parent.$("#modalOk").unbind('click').click(modal.action);
        window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').on('show.bs.modal', showSuvCourse(student)).on('hidden.bs.modal', function () {
            window.parent.$("#modalBody").empty();
            window.parent.$("#modalBody").empty();
            window.parent.$("#modalOk").unbind('click')
            window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal');
        }).modal('show');
    });
    $("#cozInfo_" + userId).click(function () {
        let modal = {
            "title": "查看督导员学生课程",
            "body": "<div class=\"container\">\n" +
            "    <form class=\"form-row course-form\" action=\"javascript:void(0)\">\n" +
            "        <div class=\"input-group input-group-sm col-4 \" style=\"margin-left: -0.5rem;margin-right: 0;padding-left:  0;padding-right:  0;\">\n" +
            "            <select class=\"custom-select form-control\" id=\"sch_year\" style='margin-left: 0.8rem;'>\n" +
            "                <option selected>请选择</option>\n" +
            "                <option value=\"2017\">2017-2018</option>\n" +
            "            </select>\n" +
            "            <div class=\"input-group-append\">\n" +
            "                <label class=\"input-group-text\" for=\"sch_year\">学年</label>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"input-group input-group-sm col-4\" style=\"margin-right: 0;padding-right: 0px;\">\n" +
            "            <select class=\"custom-select form-control\" id=\"sch_term\">\n" +
            "                <option selected>请选择</option>\n" +
            "                <option value=\"false\">上</option>\n" +
            "                <option value=\"true\">下</option>\n" +
            "            </select>\n" +
            "            <div class=\"input-group-append\">\n" +
            "                <label class=\"input-group-text mr-4\" for=\"sch_term\">学期</label>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"input-group input-group-sm col-4\" style=\"margin-left: -1.5rem;margin-right: 0.5rem;padding-right: 0;\">\n" +
            "            <div class=\"input-group-prepend\">\n" +
            "                <label class=\"input-group-text\" for=\"sch_week\">第</label>\n" +
            "            </div>\n" +
            "            <select class=\"custom-select form-control\" id=\"sch_week\">\n" +
            "                <option selected>请选择</option>\n" +
            "                <option value=\"1\">1</option>\n" +
            "                <option value=\"2\">2</option>\n" +
            "                <option value=\"3\">3</option>\n" +
            "                <option value=\"4\">4</option>\n" +
            "                <option value=\"5\">5</option>\n" +
            "                <option value=\"6\">6</option>\n" +
            "                <option value=\"7\">7</option>\n" +
            "                <option value=\"8\">8</option>\n" +
            "                <option value=\"9\">9</option>\n" +
            "                <option value=\"10\">10</option>\n" +
            "                <option value=\"11\">11</option>\n" +
            "                <option value=\"12\">12</option>\n" +
            "                <option value=\"13\">13</option>\n" +
            "                <option value=\"14\">14</option>\n" +
            "                <option value=\"15\">15</option>\n" +
            "                <option value=\"16\">16</option>\n" +
            "                <option value=\"17\">17</option>\n" +
            "                <option value=\"18\">18</option>\n" +
            "                <option value=\"19\">19</option>\n" +
            "            </select>\n" +
            "            <div class=\"input-group-append\">\n" +
            "                <label class=\"input-group-text mr-4\" for=\"sch_week\">周</label>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "        <div class=\"form-group\">\n" +
            "            <button class=\"btn btn-light btn-sm\" id=\"course_form_submit\">\n" +
            "                提交\n" +
            "            </button>\n" +
            "        </div>\n" +
            "    </form>\n" +
            "    <div class=\"row\">\n" +
            "        <div class=\"col-12\">\n" +
            "            <table class=\"table table-sm table-bordered table-hover table-responsive course-table modal-course-table\">\n" +
            "                <thead>\n" +
            "                <tr>\n" +
            "                    <th scope=\"col\">Week</th>\n" +
            "                    <th scope=\"col\">Monday</th>\n" +
            "                    <th scope=\"col\">Tuesday</th>\n" +
            "                    <th scope=\"col\">Wednesday</th>\n" +
            "                    <th scope=\"col\">Thursday</th>\n" +
            "                    <th scope=\"col\">Friday</th>\n" +
            "                    <th scope=\"col\">Saturday</th>\n" +
            "                    <th scope=\"col\">Sunday</th>\n" +
            "                </tr>\n" +
            "                </thead>\n" +
            "                <tbody>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">1</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">2</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">3</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">4</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">5</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">6</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">7</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">8</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">9</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">10</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">11</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <th scope=\"row\">12</th>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                    <td>&nbsp;</td>\n" +
            "                </tr>\n" +
            "                </tbody>\n" +
            "            </table>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>",
            "action": function () {
                window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').modal('hide');
            }
        };
        window.parent.$("#modalTitle").text(modal.title);
        window.parent.$("#modalBody").empty().append(modal.body);
        window.parent.$("#modalOk").unbind('click').click(modal.action);
        window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').on('show.bs.modal', initCozTable(student)).on('hidden.bs.modal', function () {
            window.parent.$("#modalBody").empty();
            window.parent.$("#modalBody").empty();
            window.parent.$("#modalOk").unbind('click');
            window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal');
        }).modal('show');
    })
}

function basicInfo(student) {
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/findHisSuvRecRes.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
            async: true,
            data: student.userId,
            dataType: "json",
            success:
                function (data) {
                    let hisSuvRecList = data;
                    $.ajax(
                        {
                            type: "post",
                            url: "../../Supervisor/showSuvCourses.do",
                            headers: {
                                'Access-Token': localStorage.getItem("token")
                            },
                            contentType: "application/json;charset=utf-8",
                            async: true,
                            data: student.userId,
                            dataType: "json",
                            success:
                                function (data) {
                                    let suvCourseList = data;
                                    let creditAbsScore = 0;
                                    for (let i = 0; i < suvCourseList.length; i++) {
                                        if (suvCourseList[i].suvLeave === true) {
                                            creditAbsScore++;
                                        }
                                    }
                                    window.parent.$("#stuId").text(student.userId);
                                    window.parent.$("#stuName").text(student.userName);
                                    window.parent.$("#stuSuvTime").text(hisSuvRecList.length);
                                    window.parent.$("#creditScore").text(100 - creditAbsScore);
                                },
                            error: function (err) {
                                console.log(err);
                            }
                        }
                    )
                },
            error: function (err) {
                console.log(err);
            }
        }
    )
}

function showSuvCourse(student) {
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/showSuvCourses.do",
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            data: student.userId,
            dataType: "json",
            success: function (data) {
                let suvCourseList = data;
                let statusMap = {
                    true: "正常",
                    false: "停课"
                };
                let leaveMap = {
                    true: "已请假",
                    false: "未请假"
                };
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
                                ("自动签到　" + ((node.suvMan.suvManAutoOpen == true) ? "开启" : "关闭") + "<br>　　　　&nbsp;&nbsp;人工签到时间  " + (isDeadTime(node.suvMan.siTime) ? "未开启" : node.suvMan.siTime))
                        ) +
                        "<br>排课状态: " +
                        statusMap[cozStatus] +
                        "<br>督导员请假: " +
                        leaveMap[node.suvLeave];
                    let dropdownStr = "";
                    if (node.suvMan == null) {
                        dropdownStr = dropdownStr + "<a class='dropdown-item' href='javascript:void(0)' id='initAutoSignIn_" + node.suvId + "'>开启自动签到</a>";
                        dropdownStr = dropdownStr + "<a class='dropdown-item' href='javascript:void(0)' id='initManSignIn_" + node.suvId + "'>开启人工签到</a>";
                    } else {
                        if (node.suvMan.suvManAutoOpen === true) {
                            dropdownStr = dropdownStr + "<a class='dropdown-item' href='javascript:void(0)' id='closeAutoSignIn_" + node.suvId + "'>关闭自动签到</a>";
                        } else {
                            dropdownStr = dropdownStr + "<a class='dropdown-item' href='javascript:void(0)' id='openAutoSignIn_" + node.suvId + "'>开启自动签到</a>";
                        }
                        if (isDeadTime(node.suvMan.siTime)) {
                            dropdownStr = dropdownStr + "<a class='dropdown-item' href='javascript:void(0)' id='openManSignIn_" + node.suvId + "'>开启人工签到</a>";
                        } else {
                            dropdownStr = dropdownStr + "<a class='dropdown-item' href='javascript:void(0)' id='closeManSignIn_" + node.suvId + "'>关闭人工签到</a>";
                        }
                        dropdownStr = dropdownStr + "<a class='dropdown-item' href='javascript:void(0)' id='cancelSignIn_" + node.suvId + "'>取消签到</a>";
                    }
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
                        "</td></tr>";
                    window.parent.$("#suv_sch").append(str);
                }
            },
            error: function (err) {
                console.log(err);
            }
        }
    )
}

function initCozTable(student) {
    window.parent.$("#course_form_submit").click(function () {
        selectCourseTable(window.parent.$('#sch_year').find('option:selected').val(), window.parent.$('#sch_term').find('option:selected').val(), window.parent.$('#sch_week').find('option:selected').val());
    });
    $.ajax({
        type: "post",
        url: "../../Administrator/showSuvStuCourses.do",
        headers: {
            'Access-Token': localStorage.getItem("token")
        },
        contentType: "application/json;charset=utf-8",
        async: true,
        data: student.userId,
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
    let fortnight = week % 2 == 0 ? 2 : 1;
    let courseList = selectCozSch.courses;
    for (let i = 0; i < courseList.length; i++) {
        let scheduleList = courseList[i].schedules;
        for (let j = 0; j < scheduleList.length; j++) {
            let schedule = scheduleList[j];
            if (schedule.schYear != year ||
                schedule.schTerm.toString() !== term ||
                schedule.schWeek < week ||
                (schedule.schFortnight != 0 && fortnight != schedule.schFortnight)) {
                scheduleList[j] = null;
            }
        }
    }
    insertCourseTable(selectCozSch);
}

function insertCourseTable(selectCozSch) {
    window.parent.$(".course-table tbody td").html("&nbsp;").removeAttr("rowspan").removeAttr("style").removeData("sch_id").unbind("click");
    let courseList = selectCozSch.courses;
    for (let i = 0; i < courseList.length; i++) {
        let course = courseList[i];
        let scheduleList = course.schedules;
        for (let j = 0; j < scheduleList.length; j++) {
            let schedule = scheduleList[j];
            if (schedule != null) {
                window.parent.$(".course-table tbody tr").eq(schedule.schTime - 1).children("td").eq(schedule.schDay - 1).text(course.cozName);
            }
        }
    }
    turnCourseTable();
}

function turnCourseTable() {
    let parent = window.parent.$(".course-table tbody").children("tr");
    for (let i = 0; i < 12; i++) {
        for (let j = 0; j < 7; j++) {
            let test = parent.eq(i).children("td").eq(j);
            if (test.text().trim() == '') {
                continue;
            }
            for (let CountNum = 1, row = i + 1; CountNum < 12; CountNum++, row++) {
                let node = parent.eq(row).children("td").eq(j);
                if (test.text() != node.text()) {
                    test.attr("rowspan", CountNum);
                    break;
                }
                node.hide();
            }
        }
    }
}

function modalAddSuv() {
    let modal = {
        "title": "新增督导员",
        "body": "<input class='form-control' type='text' name='user_id' id='user_id'placeholder='请输入学号'/>",
        "action": grantSuv
    };
    window.parent.$("#modalTitle").text(modal.title);
    window.parent.$("#modalBody").empty().append(modal.body);
    window.parent.$("#modalOk").unbind('click').click(modal.action);
    window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').modal('show');
}

function grantSuv() {
    let user = {
        "userId": window.parent.$("#user_id").val()
    };
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/grantSuv.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: JSON.stringify(user),
            contentType: "application/json;charset=utf-8",
            async: true,
            dataType: "json",
            success: function (data) {
                alert(JSON.stringify(data));
                location.href = location.href;
            },
            error: function (data) {
                alert(JSON.stringify(data));
                location.href = location.href;
            }
        }
    );
}

function modalRevokeSuv() {
    let modal = {
        "title": "取消一位督导员资格",
        "body": "<input class='form-control'type='text' name='user_id' id='user_id'placeholder='请输入学号'/>",
        "action": revokeSuv
    };
    window.parent.$("#modalTitle").text(modal.title);
    window.parent.$("#modalBody").empty().append(modal.body);
    window.parent.$("#modalOk").unbind('click').click(modal.action);
    window.parent.$('#indexModal').off('show.bs.modal').off('hidden.bs.modal').modal('show');
}

function revokeSuv() {
    let user = {
        "userId": window.parent.$("#user_id").val()
    };
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/revokeSuv.do",
            data: JSON.stringify(user),
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function (data) {
                alert(JSON.stringify(data));
                location.href = location.href;
            },
            error: function (data) {
                alert(JSON.stringify(data));
                location.href = location.href;
            }
        }
    );
}

function getAdmCozTable() {
    changeOrder();
}

function showAdmCozTable(courseList) {
    let jQuerySelector = $("#adm_coz_table");
    jQuerySelector.empty();
    for (let i = 0; i < courseList.length; i++) {
        let course = courseList[i];
        let scheduleList = course.schedules;
        let schStr = "";
        for (let j = 0; j < scheduleList.length; j++) {
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
            let schJoin = toZhDigit(scheduleList[j].schTime);
            while ((j !== scheduleList.length - 1) &&
            (scheduleList[j + 1].schYear === scheduleList[j].schYear) &&
            (scheduleList[j + 1].schTerm === scheduleList[j].schTerm) &&
            (scheduleList[j + 1].schWeek === scheduleList[j].schWeek) &&
            (scheduleList[j + 1].schFortnight === scheduleList[j].schFortnight) &&
            (scheduleList[j + 1].schDay === scheduleList[j].schDay) &&
            (scheduleList[j + 1].schTime - scheduleList[j].schTime === 1)) {
                schJoin += "、" + toZhDigit(scheduleList[++j].schTime);
            }
            schStr = schStr + "星期" + toZhDigit(scheduleList[j].schDay) + "　第" + schJoin + "节<br>";
        }
        schStr = schStr.substring(0, schStr.length - 4);
        let str = "<tr id='" + course.cozId + "' data-coz-size='" + course.cozSize + "' data-coz-act-size='" + course.cozActSize + "' data-coz-att-rate='" + course.cozAttRate + "'><th scope='row'>" +
            course.cozId +
            "</th><td>" +
            course.cozName +
            "</td><td>" +
            (course.teacher == null ? "&nbsp;" : course.teacher.userName) +
            "</td><td class='coz-time'>" +
            schStr +
            "</td><td class='more'><i class=\"fa fa-4x fa-angle-double-right\" aria-hidden=\"true\"></i></td></tr>";
        jQuerySelector.append(str).find("#" + replacePoint(course.cozId)).find(".more").click(function () {
            showAdmCozDetail(course.cozId);
        });
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

function zhDigitToArabic(digit) {
    const zh = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九'];
    const unit = ['千', '百', '十'];
    const quot = ['万', '亿', '兆', '京', '垓', '秭', '穰', '沟', '涧', '正', '载', '极', '恒河沙', '阿僧祗', '那由他', '不可思议', '无量', '大数'];
    let result = 0, quotFlag;

    for (let i = digit.length - 1; i >= 0; i--) {
        if (zh.indexOf(digit[i]) > -1) { // 数字
            if (quotFlag) {
                result += quotFlag * getNumber(digit[i]);
            } else {
                result += getNumber(digit[i]);
            }
        } else if (unit.indexOf(digit[i]) > -1) { // 十分位
            if (quotFlag) {
                result += quotFlag * getUnit(digit[i]) * getNumber(digit[i - 1]);
            } else {
                result += getUnit(digit[i]) * getNumber(digit[i - 1]);
            }
            --i;
        } else if (quot.indexOf(digit[i]) > -1) { // 万分位
            if (unit.indexOf(digit[i - 1]) > -1) {
                if (getNumber(digit[i - 1])) {
                    result += getQuot(digit[i]) * getNumber(digit[i - 1]);
                } else {
                    result += getQuot(digit[i]) * getUnit(digit[i - 1]) * getNumber(digit[i - 2]);
                    quotFlag = getQuot(digit[i]);
                    --i;
                }
            } else {
                result += getQuot(digit[i]) * getNumber(digit[i - 1]);
                quotFlag = getQuot(digit[i]);
            }
            --i;
        }
    }

    return result;

    // 返回中文大写数字对应的阿拉伯数字
    function getNumber(num) {
        for (let i = 0; i < zh.length; i++) {
            if (zh[i] == num) {
                return i;
            }
        }
    }

    // 取单位
    function getUnit(num) {
        for (let i = unit.length; i > 0; i--) {
            if (num == unit[i - 1]) {
                return Math.pow(10, 4 - i);
            }
        }
    }

    // 取分段
    function getQuot(q) {
        for (let i = 0; i < quot.length; i++) {
            if (q == quot[i]) {
                return Math.pow(10, (i + 1) * 4);
            }
        }
    }
}

function isDeadTime(dateTime) {
    let deadTime = JSON.stringify([1970, 1, 1, 8, 0, 1]);
    return JSON.stringify(dateTime) == deadTime;
}

function showAdmCozDetail(cozId) {
    $(".adm-body").css("overflow-y", "hidden");
    $(".container").fadeOut(500, function () {
        getData(cozId);
    }).filter(".adm-coz-detail").removeData("cozId").data("cozId", cozId).delay(500).fadeIn(500, function () {
        $(".adm-body").css("overflow-y", "auto");
    });

    function getData(cozId) {
        $("#suv_rec").empty();
        $.ajax(
            {
                type: "get",
                url: "../../Administrator/findHisAllRecRes.do",
                contentType: "application/json;charset=utf-8",
                headers: {
                    'Access-Token': localStorage.getItem("token")
                },
                data: {
                    cozId: cozId
                },
                async: true,
                dataType: "json",
                success: function (data) {
                    let hisSuvRecList = data;
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
                            hisSuvRecList[i].suvRecord.suvRecInfo +
                            "<br>缺勤名单: ";
                        let str = "<tr id='" + oneCozAndSch.schedule.schId + "_" + hisSuvRecList[i].suvWeek +
                            "' class=\"item\" data-toggle=\"collapse\" data-parent=\"#tBody\" href=\"#exampleAccordion_" +
                            oneCozAndSch.schedule.schId + "_" + hisSuvRecList[i].suvWeek + "\" aria-expanded=\"true\" aria-controls=\"exampleAccordion_" +
                            oneCozAndSch.schedule.schId + "_" + hisSuvRecList[i].suvWeek + "\" ><th scope=\"row\">" +
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
                            "<tr id=\"exampleAccordion_" + oneCozAndSch.schedule.schId + "_" + hisSuvRecList[i].suvWeek + "\" class=\"collapse\" role=\"tabpanel\" style=\"\"><td class=\"text-left rec-detail-content\" colspan=\"6\">" +
                            popoverStr +
                            "</td></tr>";
                        $("#suv_rec").append(str);
                    }
                    $.ajax(
                        {
                            type: "get",
                            url: "../../Administrator/fSchAbsRecByCoz.do",
                            contentType: "application/json;charset=utf-8",
                            headers: {
                                'Access-Token': localStorage.getItem("token")
                            },
                            data: {
                                cozId: cozId
                            },
                            async: true,
                            dataType: "json",
                            success: function (data) {
                                for (let i = 0; i < data.length; i++) {
                                    let jQuerySelector = $("#exampleAccordion_" + data[i].schedule.schId + "_" + data[i].sarWeek);
                                    if (jQuerySelector.length > 0) {
                                        let studentListStr = "";
                                        for (let j = 0; j < data[i].studentList.length; j++) {
                                            studentListStr += data[i].studentList[j].userName + ", ";
                                        }
                                        studentListStr = studentListStr.substring(0, studentListStr.length - 2);
                                        jQuerySelector.find(".rec-detail-content").append(studentListStr);
                                    } else {
                                        let jQuerySelector_2 = $("#" + replacePoint(cozId));
                                        let popoverStr = "督导号: -" +
                                            "<br>课程信息: " +
                                            jQuerySelector_2.find("td").eq(0).text() +
                                            "&nbsp;&nbsp;&nbsp;" +
                                            jQuerySelector_2.find("td").eq(1).text() +
                                            "&nbsp;&nbsp;&nbsp;" +
                                            data[i].schedule.location.locName +
                                            "<br>记录时间: " +
                                            toZhDigit(data[i].sarWeek) +
                                            "周&nbsp;&nbsp;星期" +
                                            toZhDigit(data[i].schedule.schDay) +
                                            "&nbsp;&nbsp;第" +
                                            data[i].schedule.schTime +
                                            "节<br>缺勤名单: ";
                                        let str = "<tr id='" + data[i].schedule.schId + "_" + data[i].sarWeek +
                                            "' class=\"item\" data-toggle=\"collapse\" data-parent=\"#tBody\" href=\"#exampleAccordion_" +
                                            data[i].schedule.schId + "_" + data[i].sarWeek + "\" aria-expanded=\"true\" aria-controls=\"exampleAccordion_" +
                                            data[i].schedule.schId + "_" + data[i].sarWeek + "\" ><th scope=\"row\">-</th><td>" +
                                            jQuerySelector_2.find("td").eq(0).text() +
                                            "&nbsp;&nbsp;&nbsp;" +
                                            jQuerySelector_2.find("td").eq(1).text() +
                                            "</td><td>第" +
                                            toZhDigit(data[i].sarWeek) +
                                            "周&nbsp;&nbsp;&nbsp;星期" +
                                            toZhDigit(data[i].schedule.schDay) +
                                            "&nbsp;&nbsp;&nbsp;第" +
                                            data[i].schedule.schTime +
                                            "节</td>" +
                                            "<td>-</td><td>-</td><td>-</td></tr>" +
                                            "<tr id=\"exampleAccordion_" + data[i].schedule.schId + "_" + data[i].sarWeek + "\" class=\"collapse\" role=\"tabpanel\" style=\"\"><td class=\"text-left rec-detail-content\" colspan=\"6\">" +
                                            popoverStr +
                                            "</td></tr>";
                                        $("#suv_rec").append(str);
                                        let studentListStr = "";
                                        for (let j = 0; j < data[i].studentList.length; j++) {
                                            studentListStr += data[i].studentList[j].userName + ", ";
                                        }
                                        studentListStr = studentListStr.substring(0, studentListStr.length - 2);
                                        $("#exampleAccordion_" + data[i].schedule.schId + "_" + data[i].sarWeek).find(".rec-detail-content").append(studentListStr);
                                    }
                                }
                            },
                            error: function (err) {
                                console.log(err);
                            }
                        }
                    )
                },
                error: function (err) {
                    console.log(err);
                }
            })
    }
}

function submitCozForm() {
    let cozImport = $("#insertCozInfo").serializeJSON();
    $.ajax({
        type: "post",
        url: "../../File/insertCozSch.do",
        data: JSON.stringify(cozImport),
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

function changeCozSusSch(cozId) {
    if (standardCourseList != null) {
        let courseList = standardCourseList;
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
                    `星期${toZhDigit(scheduleList[i].schDay)}　第${schJoin}节</option>`;
                window.parent.$("#schId").append(str);
            }
        }
    }
}

function showSetCozSuv() {
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        let cozId = $(".adm-coz-detail").data("cozId");
        window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
        window.parent.$("#modalTitle").text(modalMode['setCozSuv'].title);
        window.parent.$("#modalBody").empty().append(modalMode['setCozSuv'].body);
        window.parent.$("#modalOk").unbind('click').click(modalMode['setCozSuv'].action);
        changeCozSusSch(cozId);
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        window.parent.$(this).removeData("coz_id").removeData("button");
        window.parent.$("#modalBody").empty();
    });
    window.parent.$('#indexModal').data("button", $(this)).modal('show');
}

function getCozSuv() {
    let schId = window.parent.$("#schId").val().substring(1);
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/findCozSuv.do",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            contentType: "application/json;charset=utf-8",
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

function showRemoveCozSuv() {
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        let cozId = $(".adm-coz-detail").data("cozId");
        window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
        window.parent.$("#modalTitle").text(modalMode['removeCozSuv'].title);
        window.parent.$("#modalBody").empty().append(modalMode['removeCozSuv'].body);
        window.parent.$("#modalOk").unbind('click').click(modalMode['removeCozSuv'].action);
        changeCozSusSch(cozId);
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        window.parent.$(this).removeData("coz_id").removeData("button");
        window.parent.$("#modalBody").empty();
    });
    window.parent.$('#indexModal').data("button", $(this)).modal('show');
}

function removeCozSuv() {
    let schId = window.parent.$("#schId").val().substring(1);
    let suvWeek = window.parent.$("#suvWeek").val();
    let paramStr = schId + "&" + suvWeek;
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/removeCozSuv.do",
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
            url: "../../Administrator/setCozSuv.do",
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

function showSetSignIn() {
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        let cozId = $(".adm-coz-detail").data("cozId");
        window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
        window.parent.$("#modalTitle").text(modalMode['setCozSignIn'].title);
        window.parent.$("#modalBody").empty().append(modalMode['setCozSignIn'].body);
        window.parent.$("#modalOk").unbind('click').click(modalMode['setCozSignIn'].action);
        changeCozSusSch(cozId);
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        window.parent.$(this).removeData("coz_id").removeData("button");
        window.parent.$("#modalBody").empty();
    });
    window.parent.$('#indexModal').data("button", $(this)).modal('show');
}

function showRemoveSignIn() {
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        let cozId = $(".adm-coz-detail").data("cozId");
        window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
        window.parent.$("#modalTitle").text(modalMode['removeCozSignIn'].title);
        window.parent.$("#modalBody").empty().append(modalMode['removeCozSignIn'].body);
        window.parent.$("#modalOk").unbind('click').click(modalMode['removeCozSignIn'].action);
        changeCozSusSch(cozId);
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        window.parent.$(this).removeData("coz_id").removeData("button");
        window.parent.$("#modalBody").empty();
    });
    window.parent.$('#indexModal').data("button", $(this)).modal('show');
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
                url: "../../Administrator/setCozSignIn.do",
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
                url: "../../Administrator/setCozSignIn.do",
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
                url: "../../Administrator/setCozSignIn.do",
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
            url: "../../Administrator/removeCozSignIn.do",
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
            url: "../../Administrator/getCozSignIn.do",
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: schId,
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

function showCozStudent() {
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        window.parent.$(".modal-dialog").css("max-width", "70%");
        let cozId = $(".adm-coz-detail").data("cozId");
        window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
        window.parent.$("#modalTitle").text(modalMode['showCozStu'].title);
        window.parent.$("#modalBody").empty().append(modalMode['showCozStu'].body);
        window.parent.$("#modalOk").unbind('click').click(modalMode['showCozStu'].action);
        $.ajax({
            type: "get",
            url: "../../Administrator/getCozStudent.do",
            data: {
                cozId: cozId
            },
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: false,
            dataType: "json",
            success: function (data) {
                for (let i = 0; i < data.length; i++) {
                    let user = data[i];
                    window.parent.$("#adm_coz_stu").append(`<tr><td>${user.userId}</td><td>${user.userName}</td></tr>`)
                }
            },
            error: function (err) {
                console.log(err);
            }
        })
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        window.parent.$(this).removeData("coz_id").removeData("button");
        window.parent.$("#modalBody").empty();
        window.parent.$(".modal-dialog").css("max-width", "500px");
    });
    window.parent.$('#indexModal').data("button", $(this)).modal('show');
}

function showApproveLeave() {
    window.parent.$('#indexModal').off('show.bs.modal').on('show.bs.modal', function () {
        window.parent.$(".modal-dialog").css("max-width", "70%");
        let cozId = $(".adm-coz-detail").data("cozId");
        window.parent.$(this).removeData("coz_id").data("coz_id", cozId);
        window.parent.$("#modalTitle").text(modalMode['approveLeave'].title);
        window.parent.$("#modalBody").empty().append(modalMode['approveLeave'].body);
        window.parent.$("#modalOk").unbind('click').click(modalMode['approveLeave'].action);
        getLeaves(cozId);
    }).off('hidden.bs.modal').on('hidden.bs.modal', function () {
        window.parent.$(this).removeData("coz_id").removeData("button");
        window.parent.$("#modalBody").empty();
        window.parent.$(".modal-dialog").css("max-width", "500px");
    });
    window.parent.$('#indexModal').data("button", $(this)).modal('show');
}

function getLeaves(cozId) {
    $.ajax(
        {
            type: "post",
            url: "../../Administrator/getLeaves.do",
            contentType: "application/json;charset=utf-8",
            async: true,
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            data: cozId,
            dataType: "json",
            success: function (data) {
                signInResList = data;
                showLeaves(signInResList);
                window.parent.$(".leave-class").find('[data-toggle="popover"]').popover({
                    trigger: 'hover',
                    html: true,
                });
                window.parent.$(".leave-class").find(".popover-tr").hover(function () {
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
        let tr = `<tr class="leave-class"><th class='popover-tr popover-content align-middle' data-toggle='popover' data-placement='top' data-content='${popoverStr}' scope='row'>` +
            `${signInRes.siId}</th><td>${oneCozAndSch.course.cozName}<br>${oneCozAndSch.course.teacher.userName}</td>` +
            `<td>第${toZhDigit(signInRes.siWeek)}周&nbsp;&nbsp;星期${toZhDigit(oneCozAndSch.schedule.schDay)}<br>第${oneCozAndSch.schedule.schTime}节</td>` +
            `<td>${signInRes.student.userId}<br>${signInRes.student.userName}</td>` +
            `<td>${signInRes.siTime[0]}-${signInRes.siTime[1] < 10 ? "0" + signInRes.siTime[1] : signInRes.siTime[1]}-${signInRes.siTime[2] < 10 ? "0" + signInRes.siTime[2] : signInRes.siTime[2]}` +
            `<br>${signInRes.siTime[3] < 10 ? "0" + signInRes.siTime[3] : signInRes.siTime[3]}:${signInRes.siTime[4] < 10 ? "0" + signInRes.siTime[4] : signInRes.siTime[4]}:${signInRes.siTime[5] < 10 ? "0" + signInRes.siTime[5] : signInRes.siTime[5]}</td>` +
            `<td class='btn-group pt-3' id='btn_group_${signInRes.siId}'><button class='btn btn-success btn-sm' id='leave_btn_approve_${signInRes.siId}' onclick='window.frames[0].approveLeave(${signInRes.siId})'>通过</button>` +
            `<button class='btn btn-danger btn-sm' id='leave_btn_reject_${signInRes.siId}' onclick='window.frames[0].rejectLeave(${signInRes.siId})'>驳回</button></td></tr>`;
        window.parent.$("#suv_leave").append(tr);
        let id = "#btn_group_" + signInRes.siId;
        if (signInRes.siApprove == 1) {
            window.parent.$(id).empty().text("已通过");
        } else if (signInRes.siApprove == 2) {
            window.parent.$(id).empty().text("已驳回");
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
            url: "../../Administrator/approveLeave.do",
            data: JSON.stringify(signInRes),
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function () {
                let id = "#btn_group_" + siId;
                window.parent.$(id).empty().text("已通过");
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
            url: "../../Administrator/rejectLeave.do",
            data: JSON.stringify(signInRes),
            contentType: "application/json;charset=utf-8",
            headers: {
                'Access-Token': localStorage.getItem("token")
            },
            async: true,
            dataType: "json",
            success: function () {
                let id = "#btn_group_" + siId;
                window.parent.$(id).empty().text("已驳回");
            },
            error: function () {
                alert("审批失败,请稍后再试!");
            }
        }
    );
}