
// 글 삭제
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

// 글 수정
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

// 글 생성
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

// 댓글 작성
const commentCreateButton = document.getElementById('comment-create-btn');

if (commentCreateButton) {
    commentCreateButton.addEventListener('click', event => {
        boardId = document.getElementById('board-id').value;

        body = JSON.stringify({
            boardId: boardId,
            content: document.getElementById('content').value
        });
        function success() {
            alert('댓글이 작성되었습니다.');
            location.replace('/boards/' + boardId);
        };
        function fail() {
            alert('댓글 작성에 실패했습니다.');
            location.replace('/boards/' + boardId);
        };

        httpRequest('POST', '/api/comments', body, success, fail);
    });
}

// 댓글 수정
function modifyComment(commentId, button) {
    let commentParent = button.parentElement; // 부모 div
    let commentContent = document.getElementById('comment-content-' + commentId); // comment-content 가져오기

    let textarea = document.createElement('textarea'); // textarea 태그 추가
    textarea.value = commentContent.innerText; // textarea에 기존 내용 삽입

    commentParent.replaceChild(textarea, commentContent); // 기존 div를 댓글 수정을 위해 textarea로 대체

    let commentDeleteButton = document.getElementById('comment-delete-btn-' + commentId);
    commentDeleteButton.style.display = 'none'; // 삭제 버튼 안보이게

    button.innerText = '등록'; // 버튼 등록으로 변경

    button.onclick = function() { // 등록 버튼 누르면 댓글 수정
        updateComment(commentId, textarea.value, button);
    }
}

function updateComment(commentId, commentContent, button) {
    boardId = document.getElementById('board-id').value;

    body = JSON.stringify({
        content: commentContent,
    });

    function success() {
        alert("수정 완료");
        location.replace("/boards/" + boardId);
    }

    function fail() {
        alert("수정 실패");
        location.replace("/boards/" + boardId);
    }

    httpRequest("PUT", "/api/comments/" + commentId, body, success, fail);
}

// 댓글 삭제
function deleteComment(commentId) {
    let boardId = document.getElementById('board-id').value; // board의 id 저장

    function success() {
        alert("삭제 완료");
        location.replace("/boards/" + boardId);
    }
    function fail() {
        alert("삭제 실패");
        location.replace("/boards/" + boardId);
    }

    httpRequest("DELETE", "/api/comments/" + commentId, null, success, fail);
}



