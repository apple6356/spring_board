
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
            // "Content-Type": "application/json",
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

if (createNovelButton) {
    createNovelButton.addEventListener('click', event => {

        const formData = new FormData();

        formData.append("title", document.getElementById("title").value);
        formData.append("content", document.getElementById("content").value);

        const file = document.getElementById("file");

        if (file.files.length > 0) {
            formData.append("file", file.files[0]);
        }

        // body = JSON.stringify({
        //     title: document.getElementById("title").value,
        //     content: document.getElementById("content").value
        // });

        function success() {
            alert("등록 완료");
            location.replace("/novelBoard");
        }

        function fail(errorMessage) {
            alert("등록 실패, " + errorMessage);
        }

        httpRequest("POST", "/api/novel", formData, success, fail);
    });
}

// 소설 수정
const modifyNovelButton = document.getElementById('modify-novel-btn');

if (modifyNovelButton) {
    modifyNovelButton.addEventListener('click', event => {

        let id = document.getElementById("novel-id").value;

        const formData = new FormData();

        formData.append("title", document.getElementById("title").value);
        formData.append("content", document.getElementById("content").value);

        const file = document.getElementById("file");

        if (file.files.length > 0) {
            formData.append("file", file.files[0]);
        }

        // body = JSON.stringify({
        //     title: document.getElementById("title").value,
        //     content: document.getElementById("content").value
        // });

        function success() {
            alert("수정 완료");
            location.replace("/novel/" + id);
        }

        function fail() {
            alert("수정 실패");
        }

        httpRequest("PUT", "/api/novel/" + id, formData, success, fail);
    });
}

// 소설 삭제
const deleteNovelButton = document.getElementById('delete-novel-btn');

if (deleteNovelButton) {
    deleteNovelButton.addEventListener('click', event => {

        if (confirm("삭제 후 복구할 수 없습니다.\n 정말 삭제하시겠습니까?")) {
            let id = document.getElementById("novel-id").value;

            function success() {
                alert("삭제 완료");
                location.replace("/myNovels?username=" + document.getElementById("username").value);
            }

            function fail() {
                alert("삭제 실패");
            }

            httpRequest("DELETE", "/api/novel/" + id, null, success, fail);
        } else {
            return;
        }
    });
}

// 새 회차 등록
const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {


        let novelId = document.getElementById("novel-id").value,

            body = JSON.stringify({
                novelId: novelId,
                title: document.getElementById("title").value,
                content: document.getElementById("content").value
            });

        function success() {
            alert("등록 완료");
            location.replace("/novel/" + novelId);
        }

        function fail(errorMessage) {
            alert("등록 실패, " + errorMessage);
        }

        httpRequest("POST", "/api/chapter", body, success, fail);
    });
}

// 해당 회차 수정
const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {

        let novelId = document.getElementById("novel-id").value;
        let id = document.getElementById("chapter-id").value;

        body = JSON.stringify({
            novelId: novelId,
            title: document.getElementById("title").value,
            content: document.getElementById("content").value
        });

        function success() {
            alert("수정 완료");
            location.replace("/novel/" + novelId);
        }

        function fail(errorMessage) {
            alert("수정 실패, " + errorMessage);
        }

        httpRequest("PUT", "/api/chapter/" + id, body, success, fail);
    })
}

// 최신 회차 삭제
const deleteButton = document.getElementById('delete-lastepisode-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {

        if (confirm("삭제 후 복구할 수 없습니다.\n 정말 삭제하시겠습니까?")) {
            let novelId = document.getElementById('novel-id').value;

            function success() {
                alert("삭제 완료");
                location.replace("/novel/" + novelId);
            }

            function fail(errorMessage) {
                alert("삭제 실패, " + errorMessage);
            }

            httpRequest("DELETE", "/api/chapter/" + novelId, null, success, fail);
        } else {
            return;
        }
    });
}

// 최신순, 1화부터 정렬 기능
function episodeSort(e) {

    console.log("order: " + e);
    console.log("order value: " + e.value);
    let order = e.value === '최신화부터' ? '1화부터' : '최신화부터';

    $.ajax({
        type: "GET",
        url: "/episode-sort", // 요청 url
        data: { 
            novelId: document.getElementById("novel-id").value,
            order: order
        }, // 보낼 데이터
        success: function(fragment) {
            console.log("fragment : " + fragment);
            $('#chapters').replaceWith(fragment);

            if (order === '1화부터') {
                e.value = "1화부터";
            } else {
                e.value = "최신화부터";
            }

        },
        error: function() {
            alert("오류 발생");
        }
    });

}

// 댓글 작성
const commentCreateButton = document.getElementById('comment-create-btn');

if (commentCreateButton) {
    commentCreateButton.addEventListener('click', event => {
        chapterId = document.getElementById('chapter-id').value;

        body = JSON.stringify({
            chapterId: chapterId,
            content: document.getElementById('comment-content').value
        });

        function success() {
            alert('댓글이 작성되었습니다.');
            location.replace('/novelView?id=' + chapterId);
        };
        function fail() {
            alert('댓글 작성에 실패했습니다.');
        };

        httpRequest('POST', '/api/chapter-comments', body, success, fail);

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
    chapterId = document.getElementById('chapter-id').value;

    body = JSON.stringify({
        content: commentContent,
    });

    function success() {
        alert("수정 완료");
        location.replace('/novelView?id=' + chapterId);
    }

    function fail() {
        alert("수정 실패");
    }

    httpRequest("PUT", "/api/chapter-comments/" + commentId, body, success, fail);

}

// 댓글 삭제
function commentDelete(commentId) {

    if (confirm("삭제 후 복구할 수 없습니다. \n 정말 삭제하시겠습니까?")) {
        let chapterId = document.getElementById('chapter-id').value; // board 의 id 저장

        function success() {
            alert("삭제 완료");
            location.replace('/novelView?id=' + chapterId);
        }
        function fail() {
            alert("삭제 실패");
        }

        httpRequest("DELETE", "/api/chapter-comments/" + commentId, null, success, fail);
    } else {
        return;
    }

}

// 댓글 추천+1
function commentRecommend(commentId) {
    let chapterId = document.getElementById('chapter-id').value;

    function success() {
        alert("추천 완료");
        location.replace('/novelView?id=' + chapterId);
    }
    function fail() {
        alert("추천 실패");
    }

    httpRequest("PUT", "/api/chapter-comments-recommend/" + commentId, null, success, fail);

}


// $.ajax({
//     type: "GET",
//     url: "/check", // 요청 url
//     data: { username: username }, // 보낼 데이터
//     success: function(data) {
//         console.log("data : " + data)
//         if (data) {
//             document.getElementById('check-result').textContent = "중복입니다.";
//             document.getElementById('check-result').style.color = "red";
//         } else {
//             document.getElementById('check-result').textContent = "사용 가능합니다.";
//             document.getElementById('check-result').style.color = "green";
//         }
//     },
//     error: function() {
//         alert("오류 발생");
//     }
// });


