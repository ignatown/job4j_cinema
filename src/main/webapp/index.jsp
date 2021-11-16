<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
            integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://code.jquery.com/jquery-3.4.1.min.js" ></script>
    <script>
        $(document).ready(function() {
            show();
            setInterval ('show()',36000);
        });

        function show() {
            $.ajax({
                type: 'GET',
                url: 'http://localhost:8080/cinema/hall',
                dataType: 'json'
            }).done(function(data) {
                $('#table').empty();
                for (let row = 1; row <= 3; row++) {
                    let section = '<tr><th>' + row +  '</th>';
                    for (let cell = 1; cell <= 3; cell++) {
                        let ids = parseInt("" + row + cell);
                        let added = true;
                        for (let i = 0; i < data.length; i++) {
                            if (data[i].id === ids) {
                                section += '<td id="' + data[i].id + '"><input disabled="disabled" ' +
                                    'type="checkbox" name="place" value="' + data[i].id + '"><b> ЗАНЯТО </b></td>';
                                added = false;
                                break;
                            }
                        }
                        if (added) {
                            section += '<td id="'+ ids +'"><input id="'+ ids +'" '
                                + 'type="checkbox" name="place" value="' + 'Ряд ' + row + ', Место '
                                + cell + '"> ' + 'Ряд ' + row + ', Место ' + cell + '</td>';
                        }
                    }
                    $('#table').append(section);
                }
            }).fail(function(err){
                alert(err);
            });
        }
        function pay() {
            let element = {};
            $('input[name="place"]:checked').each(
                function () {
                    console.log(this.id);
                    element[this.id] = this.value;
                }
            );
            sessionStorage.setItem("place", JSON.stringify(element));
            if (Object.keys(element).length !== 0) {
                window.location.href = 'payment.jsp';
            }
        }
    </script>
    <title>Job4J CINEMA</title>
</head>
<body>
<div class="container">
    <div class="row pt-3">
        <h4>
            Бронирование месте на сеанс
        </h4>
        <table class="table table-bordered">
            <thead>
            <tr>
                <th style="width: 120px;">Ряд / Место</th>
                <th>1</th>
                <th>2</th>
                <th>3</th>
            </tr>
            </thead>
            <tbody id="table">
            </tbody>
        </table>
    </div>
    <div style="text-align: center;">
        <button type="button" class="btn btn-success" onclick="pay()">Оплатить</button>
    </div>
</div>
</body>
</html>