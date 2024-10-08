
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
        headers: {
            // 로컬 스토리지에서 액세스 토큰 값을 가져와 헤더에 추가
            Authorization: "Bearer " + localStorage.getItem("access_token"),
            "Content-Type": "application/json",
        },
        body: body,
    })
        .then((response) => {
            if (response.status === 200 || response.status === 201) {
                return response.json();
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
        })
        .then(data => {
            // if (data) 는 데이터가 truthy한 값인지 확인, null, undefined, 0, 빈 문자열, false 등인지 확인
            if (data) success(data);
        });
}

// 글 생성
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {

        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: $('#content').summernote('code')
            //            content: document.getElementById("content").value
        });

        function success() {
            alert("등록 완료");
            location.replace("/boards");
        }

        function fail(errorMessage) {
            alert("등록 실패, " + errorMessage);
            //            location.replace("/boards");
        }

        httpRequest("POST", "/api/boards", body, success, fail);
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

        body = JSON.stringify({
            title: document.getElementById("title").value,
            content: $('#content').summernote('code')
            //            content: document.getElementById("content").value,
        });

        function success() {
            alert("수정 완료");
            location.replace("/boards/" + id);
        }

        function fail() {
            alert("수정 실패");
            //            location.replace("/boards/" + id);
        }

        httpRequest("PUT", "/api/boards/" + id, body, success, fail);
    })
}

// 글 삭제
const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    console.log("delete btn start");
    deleteButton.addEventListener('click', event => {

        if (confirm("삭제 후 복구할 수 없습니다.\n 정말 삭제하시겠습니까?")) {
            let id = document.getElementById('board-id').value;

            function success() {
                alert("삭제 완료");
                location.replace("/boards");
            }

            function fail() {
                alert("삭제 실패");
                //                location.replace("/boards");
            }

            httpRequest("DELETE", "/api/boards/" + id, null, success, fail);
        } else {
            return;
        }

    });
}

// 글 추천+1
const recommendButton = document.getElementById('recommend-btn');

if (recommendButton) {
    recommendButton.addEventListener('click', event => {
        let id = document.getElementById("board-id").value;

        function success(data) {
            alert("추천 완료");
            // location.replace("/boards/" + id);
            console.log("data: " + data);
            document.getElementById("recommend").innerText = data.recommend;
        }

        function fail() {
            alert("추천 실패");
            location.replace("/boards/" + id);
        }

        httpRequest("PUT", "/api/recommend/" + id, null, success, fail);

    });
}

// 댓글 리스트 갱신
function updateCommentList(data) {
    const commentList = document.getElementById("comment-list");
    const popularCommentList = document.getElementById("popular-comment-list");

    commentList.innerHTML = '';
    popularCommentList.innerHTML = '';

    data.forEach(comment => {
        const commentDiv = document.createElement('div');
        const currentUser = document.getElementById('current-user').value;

        console.log("currentUser: " + currentUser);
        console.log("comment.author: " + comment.author);

        commentDiv.innerHTML = `
            <div>작성자 : ${comment.author}, 작성 시간 : ${new Date(comment.createdAt).toLocaleString()}</div>

            <button type="button" onclick="commentRecommend(${comment.id})">추천</button>

            <span id="comment-recommend-${comment.id}">${comment.recommend}</span> <br>

            <div id="comment-content-${comment.id}">${comment.content}</div>

            ${currentUser === comment.author ? `
                <button type="button" id="modify-comment-btn-${comment.id}" onclick="commentModify(${comment.id}, this)">수정</button>
                <button type="button" id="delete-comment-btn-${comment.id}" onclick="commentDelete(${comment.id})">삭제</button>
            ` : ''}
        `;

        if (comment.recommend >= 10) {
            popularCommentList.appendChild(commentDiv.cloneNode(true));
        }

        commentList.appendChild(commentDiv);
        commentList.appendChild(document.createElement('hr'));
    });
}

// 댓글 작성
const commentCreateButton = document.getElementById('comment-create-btn');

if (commentCreateButton) {
    commentCreateButton.addEventListener('click', event => {
        boardId = document.getElementById('board-id').value;

        body = JSON.stringify({
            boardId: boardId,
            content: document.getElementById('comment-content-write').value
        });

        function success(data) {
            alert('댓글이 작성되었습니다.');
            // location.replace('/boards/' + boardId);
            document.getElementById('comment-content-write').value = '';
            updateCommentList(data);
        };
        function fail() {
            alert('댓글 작성에 실패했습니다.');
            // location.replace('/boards/' + boardId);
        };

        httpRequest('POST', '/api/comments', body, success, fail);

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

    button.onclick = function () { // 등록 버튼 누르면 댓글 수정
        updateComment(commentId, textarea.value, button);
    }
}

// 댓글 수정
function updateComment(commentId, commentContent, button) {
    boardId = document.getElementById('board-id').value;

    body = JSON.stringify({
        content: commentContent,
    });

    function success(data) {
        alert("수정 완료");
        // location.replace("/boards/" + boardId);
        updateCommentList(data);
    }

    function fail() {
        alert("수정 실패");
        // location.replace("/boards/" + boardId);
    }

    httpRequest("PUT", "/api/comments/" + commentId, body, success, fail);

}

// 댓글 삭제
function commentDelete(commentId) {

    if (confirm("삭제 후 복구할 수 없습니다. \n 정말 삭제하시겠습니까?")) {
        let boardId = document.getElementById('board-id').value; // board 의 id 저장

        function success(data) {
            alert("삭제 완료");
            // location.replace("/boards/" + boardId);
            updateCommentList(data);
        }
        function fail() {
            alert("삭제 실패");
            // location.replace("/boards/" + boardId);
        }

        httpRequest("DELETE", "/api/comments/" + commentId, null, success, fail);
    } else {
        return;
    }

}

// 댓글 추천+1
function commentRecommend(commentId) {
    let boardId = document.getElementById('board-id').value;
    let recommend = document.getElementById('comment-recommend-' + commentId).value;

    body = JSON.stringify({
        recommend: recommend,
    });

    function success(data) {
        alert("추천 완료");
        // location.replace("/boards/" + boardId);
        updateCommentList(data);
    }
    function fail() {
        alert("추천 실패");
        // location.replace("/boards/" + boardId);
    }

    httpRequest("PUT", "/api/comment-recommend/" + commentId, body, success, fail);

}



