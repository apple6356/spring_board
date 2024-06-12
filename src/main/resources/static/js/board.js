const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('board-id').value;

        function success() {
            alert("삭제 완료");
            location.replace("/boards");
        }

        function fail() {
            alert("삭제 실패");
            location.replace("/boards");
        }

        httpRequest("DELETE", "/api/boards/" + id, null, success, fail);
    });
}

const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id')

        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
        });

        function success() {
            alert("수정 완료");
            location.replace("/boards/" + id);
        }

        function fail() {
            alert("수정 실패");
            location.replace("/boards/" + id);
        }

        httpRequest("PUT", "/api/boards/" + id, body, success, fail);
    })
}

const createButton = document.getElementById('create-btn');

if(createButton) {
    createButton.addEventListener('click', event => {
        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
        });

        function success() {
            alert("등록 완료");
            location.replace("/boards");
        }

        function fail() {
            alert("등록 실패");
            location.replace("/boards");
        }

        httpRequest("POST", "/api/boards", body, success, fail);
    });
}

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
            return success();
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
            return fail();
        }
    });
}

