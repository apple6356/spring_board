
// 쿠키를 가져오는 함수
function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(";");

    cookie.some(function (item) {
        item = item.trim();

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
//        headers: headers,
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
                httpRequest(method, url, body, false, success, fail);
            })
            .catch((error) => fail(error.message));
        } else {
            return fail();
        }
    });
}

// 소설 등록
const createNovelButton = document.getElementById('create-novel-btn');

if(createNovelButton) {
    createNovelButton.addEventListener('click', event => {

        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value
        });

        function success() {
            alert("등록 완료");
            location.replace("/novelBoard");
        }

        function fail(errorMessage) {
            alert("등록 실패, " + errorMessage);
        }

        httpRequest("POST", "/api/novel", body, success, fail);
    });
}

// 소설 수정
const modifyNovelButton = document.getElementById('modify-novel-btn');

if (modifyNovelButton) {
    modifyNovelButton.addEventListener('click', event => {

        let id = document.getElementById("novel-id").value;

        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value
        });

        function success() {
            alert("수정 완료");
            location.replace("/novel/" + id);
        }

        function fail() {
            alert("수정 실패");
        }

        httpRequest("PUT", "/api/novel/" + id, body, success, fail);
    });
}

// 소설 각 화별 등록
const createButton = document.getElementById('create-btn');

if(createButton) {
    createButton.addEventListener('click', event => {

        let novelId = document.getElementById("novel-id").value;

        body = JSON.stringify({
            novelId: novelId,
            title: document.getElementById("title").value,
            content: $('#content').summernote('code')
        });

        function success() {
            alert("등록 완료");
            location.replace("/novel/" + novelId);
        }

        function fail(errorMessage) {
            alert("등록 실패, " + errorMessage);
        }

        httpRequest("POST", "/api/novel-write", body, success, fail);
    });
}

// 소설 각 화별 수정
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {

        let novelId = document.getElementById("novel-id").value;
        let id = document.getElementById("novel-episode-id").value;

        body = JSON.stringify({
            novelId: novelId,
            title: document.getElementById("title").value,
            content: $('#content').summernote('code')
        });

        function success() {
            alert("수정 완료");
            location.replace("/novel/" + novelId);
        }

        function fail() {
            alert("수정 실패");
        }

        httpRequest("PUT", "/api/novel-write/" + id, body, success, fail);
    })
}

