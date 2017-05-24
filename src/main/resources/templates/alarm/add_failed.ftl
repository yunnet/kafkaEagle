<!DOCTYPE html>
<html lang="zh">

<head>
<title>Failed - KafkaEagle</title>
<#include "../public/css.ftl" encoding="UTF-8">
</head>

<body>
	<#include "../public/navbar.ftl" encoding="UTF-8">
	<div id="wrapper">
		<div id="page-wrapper">
			<div class="row">
				<div class="col-lg-12">
					<h1 class="page-header">
						Alarm <small>create</small>
					</h1>
				</div>
				<!-- /.col-lg-12 -->
			</div>
			<!-- /.row -->
			<div class="row">
				<div class="col-lg-12">
					<div class="alert alert-danger alert-dismissable">
						<button type="button" class="close" data-dismiss="alert"
							aria-hidden="true">×</button>
						<i class="fa fa-info-circle"></i> <strong>${Alarm_Submit_Status}</strong>
						<a class="btn btn-large btn-primary" href="/ke/alarm/add">
							<span class="ui-button-text">Create Again</span>
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>

<#include "../public/script.ftl">
<script src="${springMacroRequestContext.contextPath}/static/js/main/add.success.js" type="text/javascript"></script>

</html>