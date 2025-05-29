<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
        <meta name="description" content="" />
        <meta name="author" content="" />
        <title>Dashboard - THE SOBAT GAPAI (Admin)</title>
        <link href="https://cdn.jsdelivr.net/npm/simple-datatables@7.1.2/dist/style.min.css" rel="stylesheet" />
        <link href="{{asset('startbootstrap-sb-admin-gh-pages/css/styles.css')}}" rel="stylesheet" />
        <script src="https://use.fontawesome.com/releases/v6.3.0/js/all.js" crossorigin="anonymous"></script>
    </head>
    <body class="sb-nav-fixed">
        @extends('admin.partials.navbar')
        <div id="layoutSidenav">
            <div id="layoutSidenav_nav">
                <nav class="sb-sidenav accordion sb-sidenav-dark" id="sidenavAccordion">
                    <div class="sb-sidenav-menu">
                        <div class="nav">
                            <div class="sb-sidenav-menu-heading">Core</div>
                            <a class="nav-link" href="{{route('admin.dashboard')}}">
                                <div class="sb-nav-link-icon"><i class="fas fa-home"></i></div>
                                Dashboard
                            </a>
                            <div class="sb-sidenav-menu-heading">Umum</div>
                            <a class="nav-link collapsed" href="{{route('admin.laporan')}}">
                                <div class="sb-nav-link-icon"><i class="fas fa-exclamation-circle"></i></div>
                                Laporan
                                <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                            </a>
                                <a class="nav-link collapsed" href="{{route('admin.tugas')}}" >
                                    <div class="sb-nav-link-icon"><i class="fas fa-tasks"></i></div>
                                    Tugas
                                    <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                                </a>
                                <a class="nav-link collapsed" href="{{route('admin.jawaban')}}">
                                    <div class="sb-nav-link-icon"><i class="fas fa-reply"></i></div>
                                    Jawaban
                                    <div class="sb-sidenav-collapse-arrow"><i class="fas fa-angle-down"></i></div>
                                </a>
                        </div>
                    </div>
                    <div class="sb-sidenav-footer">
                        <div class="small">THE SOBAT GAPAI</div>
                    </div>
                </nav>
            </div>
            <div id="layoutSidenav_content">
                <main>
                    <div class="container-fluid px-4">
                        <h1 class="mt-4">Laporan</h1>
                        <ol class="breadcrumb mb-4">
                            <li class="breadcrumb-item active">Admin</li>
                        </ol>
                        <div class="card mb-4">
                            <div class="card-header">
                                <i class="fas fa-table me-1"></i>
                                DataTable Laporan
                            </div>
                            <div class="card-body">
                                <table id="datatablesSimple" class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>No</th>
                                            <th>User (pelapor)</th>
                                            <th>ID Tugas</th>
                                            <th>ID Jawaban</th>
                                            <th>Judul Laporan</th>
                                            <th>Deskripsi</th>
                                            <th>Tanggal Dibuat</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        @foreach ($reports as $index => $report)
                                            <tr>
                                                <td>{{ $index + 1 }}</td>
                                                <td>{{ $report->user->name }}</td> <!-- Asumsi kolom name ada di tabel users -->
                                                <td>{{ $report->tugas->id }}</td> <!-- Asumsi kolom judul ada di tabel tugas -->
                                                <td>{{ $report->answer ? $report->answer->id : '(No Answer)' }}</td> <!-- Asumsi kolom jawaban ada di tabel answers -->
                                                <td>{{ $report->title }}</td>
                                                <td>{{ $report->description }}</td>
                                                <td>{{ $report->created_at->format('d M Y H:i') }}</td>
                                            </tr>
                                        @endforeach
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </main>
                <footer class="py-4 bg-light mt-auto">
                    <div class="container-fluid px-4">
                        <div class="d-flex align-items-center justify-content-between small">
                            <div class="text-muted">Copyright &copy; THE SOBAT GAPAI 2025</div>
                        </div>
                    </div>
                </footer>
            </div>
        </div>
        <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
        <script>
            $('.ban-user').on('click', function () {
    const userId = $(this).data('user-id');
    console.log("Ban user with ID:", userId); // Debugging
    if (confirm('Apakah Anda yakin ingin memban user ini?')) {
        $.ajax({
            url: `/admin/users/${userId}/ban`,
            method: 'POST',
            data: {
                _token: '{{ csrf_token() }}'
            },
            success: function (response) {
                console.log("Ban response:", response); // Debugging
                alert(response.message);
                location.reload();
            },
            error: function (xhr) {
                console.error("Ban error:", xhr.responseJSON); // Debugging
                alert('Terjadi kesalahan: ' + xhr.responseJSON.message);
            }
        });
    }
});

$('.delete-user').on('click', function () {
    const userId = $(this).data('user-id');
    console.log("Delete user with ID:", userId); // Debugging
    if (confirm('Apakah Anda yakin ingin menghapus user ini?')) {
        $.ajax({
            url: `/admin/users/${userId}/delete`,
            method: 'DELETE',
            data: {
                _token: '{{ csrf_token() }}'
            },
            success: function (response) {
                console.log("Delete response:", response); // Debugging
                alert(response.message);
                location.reload();
            },
            error: function (xhr) {
                console.error("Delete error:", xhr.responseJSON); // Debugging
                alert('Terjadi kesalahan: ' + xhr.responseJSON.message);
            }
        });
    }
});
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/js/scripts.js')}}"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js" crossorigin="anonymous"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/assets/demo/chart-area-demo.js')}}"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/assets/demo/chart-bar-demo.js')}}"></script>
        <script src="https://cdn.jsdelivr.net/npm/simple-datatables@7.1.2/dist/umd/simple-datatables.min.js" crossorigin="anonymous"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/js/datatables-simple-demo.js')}}"></script>
    </body>
</html>
