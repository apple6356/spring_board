

// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(";");

    cookie.some(function (item) {
        item = item.replace(" ", "");

        var dic = item.split("=");

        if (key === dic[0]) { // ===은 데이터 타입까지 같아야 함
            result = dic[1];
            return true;
        }
    });

    return result;
}

// Http 요청을 보내는 함수
function httpRequest(method, url, body, success, fail) {

    fetch(url, {
        method: method,
        headers : {
            // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: "Bearer " + localStorage.getItem("access_token"),
            "Content-Type": "application/json",
        },
        body: body,
    })
    .then((response) => {
        if (response.status === 200 || response.status === 201) {
//            return success(response);
            return response.json().then(data => success(data));
        }
        const refresh_token = getCookie("refresh_token");
        if (response.status === 401 && refresh_token) {
            fetch("/api/token", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + localStorage.getItem("access_token"),
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    refreshToken: getCookie("refresh_token"),
                }),
            })
            .then((res) => res.json()) // 응답을 JSON으로 파싱
            .then((result) => {
                // 재발급이 성공하면 로컬 스토리지값을 새 액세스 토큰으로 교체
                localStorage.setItem("access_token", result.accessToken);
                httpRequest(method, url, body, success, fail);
            })
            .catch((error) => fail());
        } else {
            return fail(response);
        }
    });
}


// 유저 네임 변경
const updateUserButton = document.getElementById('update-user-btn');

if (updateUserButton) {
    updateUserButton.addEventListener('click', event => {

        let id = document.getElementById('user-id').value;

        body = JSON.stringify({
            username : document.getElementById('username').value
        });

        function success() {
            alert("수정 완료");
            location.replace("/info");
        }

        function fail() {
            alert("수정 실패");
            location.replace("/info");
        }

        httpRequest("PUT", "/api/user/" + id, body, success, fail);
    });
}

// username 중복 확인
const nameCheckButton = document.getElementById('name-check-btn');

if (nameCheckButton) {
    nameCheckButton.addEventListener('click', event => {
        console.log($);

        let username = document.getElementById('username').value;

        console.log("username : " + username);

        $.ajax({
            type: "GET",
            url: "/api/check", // 요청 url
            data: { username: username }, // 보낼 데이터
            success: function(data) {
                if (data) {
                    document.getElementById('check-result').textContent = "중복입니다.";
                    document.getElementById('check-result').style.color = "red";
                } else {
                    document.getElementById('check-result').textContent = "사용 가능합니다.";
                    document.getElementById('check-result').style.color = "green";
                }
            },
            error: function() {
                alert("오류 발생");
            }
        });

//        function success(data) {
//            if (data) {
//                alert("사용 가능합니다.");
//                document.getElementById('check-result').textContent = "사용 가능합니다.";
//            } else {
//                alert("중복입니다.");
//                document.getElementById('check-result').textContent = "중복입니다.";
//            }
//         }
//
//         function fail(error) {
//            alert("오류 발생");
//         }
//
//         httpRequest("GET", "/api/check/" + username, null, success, fail);

    });
}


