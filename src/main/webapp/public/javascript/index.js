"use strict";

let user;

$(function () {
    readCookie();
    showOption();
    $("#logout").click(function () {
        $.ajax(
            {
                url: "./logout.do",
                async: true,
                headers: {
                    'Access-Token': localStorage.getItem("token")
                },
                dataType: "json",
                success: function () {
                    user = null;
                    localStorage.removeItem('user');
                    window.location.href = "./login.html";
                },
                error: function (data) {
                    console.log(data);
                    user = null;
                    localStorage.removeItem('user');
                    window.location.href = "./login.html";
                }
            }
        );
    });
    $("#setting").click(function () {
        $("#nav-tab").empty();
        changeIFrame("./setting.html")
    })
});

function readCookie() {
    $.ajax({
        type: "get",
        url: "./checkLogin.do",
        async: true,
        headers: {
            'Access-Token': localStorage.getItem("token")
        },
        dataType: "json",
        success: function (data) {
            if (data === false) {
                user = null;
                localStorage.removeItem('user');
                localStorage.removeItem('token');
                window.location.href = "./login.html";
            }
        },
        error: function () {
            user = null;
            localStorage.removeItem('user');
            alert("网络错误! 将回到登录页!");
            window.location.href = "./login.html";
        }
    });
    user = JSON.parse(localStorage.getItem("user"));
    if (user == null) {
        window.location.href = "./login.html";
    }
    else {
        user.userPermit = parseInt(user.userPermit, 2);
        $("#user_id").text(user.userName);
    }
}

function showOption() {
    let permit = user.userPermit;
    if ((permit & 1) === 1) {
        $("#student").show();
    }
    if ((permit & 2) === 2) {
        $("#supervisor").show();
    }
    if ((permit & 4) === 4) {
        $("#teacher").show();
    }
    if ((permit & 8) === 8) {
        $("#administrator").show();
    }
    if ((permit & 16) === 16) {
        $("#suAdministrator").show();
    }
    $(".permit").find("a:visible").eq(0).click();
}

function hideBgBrand() {
    $(".nav-tab").show();
    $(".main").show();
    $(".bg-brand").hide();
}

function student() {
    $.getJSON("./public/javascript/student.json", function (data) {
        changeSideBar(data);
    });
    $(".permit a").removeClass("active");
    $("#student").addClass("active");
    hideBgBrand();
}

function supervisor() {
    $.getJSON("./public/javascript/supervisor.json", function (data) {
        changeSideBar(data);
    });
    $(".permit a").removeClass("active");
    $("#supervisor").addClass("active");
    hideBgBrand();
}

function teacher() {
    $.getJSON("./public/javascript/teacher.json", function (data) {
        changeSideBar(data);
    });
    $(".permit a").removeClass("active");
    $("#teacher").addClass("active");
    hideBgBrand();
}

function administrator() {
    $.getJSON("./public/javascript/administrator.json", function (data) {
        changeSideBar(data);
    });
    $(".permit a").removeClass("active");
    $("#administrator").addClass("active");
    hideBgBrand();
}

function suAdministrator() {
    $.getJSON("./public/javascript/suAdministrator.json", function (data) {
        changeSideBar(data);
    });
    $(".permit a").removeClass("active");
    $("#suAdministrator").addClass("active");
    hideBgBrand();
}

function changeSideBar(data) {
    let s1 = $("#nav-tab");
    s1.empty();
    let sideBarId = 0;
    Object.keys(data).forEach(function (key) {
        let keyId = "sideBar_" + sideBarId++;
        let href = data[key];
        let str = "<a class=\"nav-link\" id=\"";
        str += keyId;
        str += "\" href=\"javascript:void(0)\" onclick=\"changeIFrame(\'";
        str += href;
        str += "\')\" role=\"tab\">";
        str += key;
        str += "</a>";
        $("#nav-tab").append(str);
    });
    $(".nav-tab a").on('click', function () {
        $(".nav-tab a").removeClass("active");
        $(this).addClass("active");
    });
    s1.find("a:first-of-type").click();
    s1.find("a").hide().fadeIn(500);
}

function changeIFrame(hrefStr) {
    let s1 = $("#main-frame");
    s1.attr("src", hrefStr);
    s1.hide().fadeIn(800);
}


