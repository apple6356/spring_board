
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
function httpRequest(method, url, body, isMultipart, success, fail) {
    const headers = {
        Authorization: "Bearer " + localStorage.getItem("access_token"),
    };

    if (isMultipart == false){
        headers["Content-Type"] = "application/json";
    }

    fetch(url, {
        method: method,
        headers: headers,
//        headers : {
//            // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
//            Authorization: "Bearer " + localStorage.getItem("access_token"),
////            "Content-Type": "application/json",
//        },
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
            .catch((error) => fail());
        } else {
            return fail();
        }
    });
}

// 글 삭제
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {

        if (confirm("삭제 후 복구할 수 없습니다.\n 정말 삭제하시겠습니까?")) {
            let id = document.getElementById('board-id').value;

            function success() {
                alert("삭제 완료");
                location.replace("/boards");
            }

            function fail() {
                alert("삭제 실패");
                location.replace("/boards");
            }

            httpRequest("DELETE", "/api/boards/" + id, null, false, success, fail);
        } else {
            return;
        }


    });
}

// 글 수정
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        // URLSearchParams : 주소창의 url 을 다룰 수 있음
        // location.search : url 의 파라미터를 가져올 수 있음
        let params = new URLSearchParams(location.search);
        let id = params.get('id') // 주소창의 id 파라미터를 가져옴

//        let formData = new FormData();
//
//        formData.append('board', new Blob([JSON.stringify({
//            title: document.getElementById("title").value,
//            content: document.getElementById("content").value
//        })], { type: "application/json" }));

//        let files = document.getElementById("files").files;
//
//        let isMultipart = true;
//        if (files == null) {
//            isMultipart = false;
//        }
//
//        for(let i = 0; i < files.length; i++) {
//            formData.append('files', files[i]);
//        }

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

        httpRequest("PUT", "/api/boards/" + id, body, false, success, fail);
    })
}

// 글 생성
const createButton = document.getElementById('create-btn');

if(createButton) {
    createButton.addEventListener('click', event => {
        let formData = new FormData();

        // JSON 데이터를 직렬화하여 Blob으로 만들어 FormData에 추가
//        let boardData = {
//            title: document.getElementById("title").value,
//            content: document.getElementById("content").value
//        };
//
//        formData.append('board', new Blob([JSON.stringify(boardData)], { type: "application/json" }));


        formData.append('board', new Blob([JSON.stringify({
            title: document.getElementById("title").value,
            content: document.getElementById("content").value
        })], { type: "application/json" }));
//
//        formData.append('title', document.getElementById("title").value);
//        formData.append('content', document.getElementById("content").value);

        let files = document.getElementById("files").files;

        let isMultipart = true;
        if (files == null) {
            isMultipart = false;
        }

        for(let i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }

//        body = JSON.stringify({
//        });

        function success() {
            alert("등록 완료");
            location.replace("/boards");
        }

        function fail() {
            alert("등록 실패");
            location.replace("/boards");
        }

        httpRequest("POST", "/api/boards", formData, isMultipart, success, fail);
    });
}

// 글 추천+1
const recommendButton = document.getElementById('recommend-btn');

if (recommendButton) {
    recommendButton.addEventListener('click', event => {
        let id = document.getElementById("board-id").value;

        function success() {
            alert("추천 완료");
            location.replace("/boards/" + id);
        }

        function fail() {
            alert("추천 실패");
            location.replace("/boards/" + id);
        }

        httpRequest("PUT", "/api/recommend/" + id, null, false, success, fail);

    });
}

// 댓글 작성
const commentCreateButton = document.getElementById('comment-create-btn');

if (commentCreateButton) {
    commentCreateButton.addEventListener('click', event => {
        boardId = document.getElementById('board-id').value;

//        let formData = new FormData();
//
//        formData.append('comment', new Blob([JSON.stringify({
//            boardId: boardId,
//            content: document.getElementById('content').value
//        })], { type: "application/json" }));
//
//        let files = document.getElementById("files").files;
//
//        for(let i = 0; i < files.length; i++) {
//            formData.append('files', files[i]);
//        }

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

        httpRequest('POST', '/api/comments', body, false, success, fail);

    });
}

// 댓글 수정 화면
function commentModify(commentId, button) {
    let commentParent = button.parentElement; // 부모 div
    let commentContent = document.getElementById('comment-content-' + commentId); // comment-content 가져오기

    let textarea = document.createElement('textarea'); // textarea 태그 추가
    textarea.value = commentContent.innerText; // textarea 에 기존 내용 삽입

    commentParent.replaceChild(textarea, commentContent); // 기존 div를 댓글 수정을 위해 textarea로 대체

    let commentDeleteButton = document.getElementById('delete-comment-btn-' + commentId);
    commentDeleteButton.style.display = 'none'; // 삭제 버튼 안보이게

    button.innerText = '등록'; // 버튼 등록으로 변경

    button.onclick = function() { // 등록 버튼 누르면 댓글 수정
        updateComment(commentId, textarea.value, button);
    }
}

// 댓글 수정
function updateComment(commentId, commentContent, button) {
    boardId = document.getElementById('board-id').value;

//    let formData = new FormData();
//
//    formData.append('comment', new Blob([JSON.stringify({
//        content: document.getElementById('content').value
//    })], { type: "application/json" }));

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

    httpRequest("PUT", "/api/comments/" + commentId, body, false, success, fail);

}

// 댓글 삭제
function commentDelete(commentId) {

    if (confirm("삭제 후 복구할 수 없습니다. \n 정말 삭제하시겠습니까?")) {
        let boardId = document.getElementById('board-id').value; // board 의 id 저장

        function success() {
            alert("삭제 완료");
            location.replace("/boards/" + boardId);
        }
        function fail() {
            alert("삭제 실패");
            location.replace("/boards/" + boardId);
        }

        httpRequest("DELETE", "/api/comments/" + commentId, null, false, success, fail);
    } else {
        return;
    }

}

// 댓글 추천+1
function commentRecommend(commentId) {
    let boardId = document.getElementById('board-id').value;
    let recommend = document.getElementById('comment-recommend-' + commentId).value;

//    let formData = new FormData();
//
//    formData.append('comment', new Blob([JSON.stringify({
//        recommend: recommend,
//    })], { type: "application/json" }));

    body = JSON.stringify({
        recommend: recommend,
    });

    function success() {
        alert("추천 완료");
        location.replace("/boards/" + boardId);
    }
    function fail() {
        alert("추천 실패");
        location.replace("/boards/" + boardId);
    }

    httpRequest("PUT", "/api/comment-recommend/" + commentId, body, false, success, fail);

}



