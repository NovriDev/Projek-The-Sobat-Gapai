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
                        <h1 class="mt-4">Dashboard</h1>
                        <ol class="breadcrumb mb-4">
                            <li class="breadcrumb-item active">Admin</li>
                        </ol>
                        <div class="row m-auto">
                            <div class="col-xl-3 col-md-6">
                                <div class="card bg-primary text-white mb-4">
                                    <div class="card-body">Total Akun</div>
                                    <div class="card-footer d-flex align-items-center justify-content-between">
                                    <div class="card-body fs-8 text-center">{{ $totalAccounts }}</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-xl-3 col-md-6">
                                <div class="card bg-success text-white mb-4">
                                    <div class="card-body">Data Tugas</div>
                                    <div class="card-footer d-flex align-items-center justify-content-between">
                                        <div class="card-body fs-8 text-center">{{ $totalTugas }}</div>
                                        </div>
                                </div>
                            </div>
                            <div class="col-xl-3 col-md-6">
                                <div class="card bg-danger text-white mb-4">
                                    <div class="card-body">Laporan Pengguna</div>
                                    <div class="card-footer d-flex align-items-center justify-content-between">
                                        <div class="card-body fs-8 text-center">{{ $totalReports }}</div>
                                        </div>
                                </div>
                            </div>
                        </div>
                        <div class="card mb-4">
                            <div class="card-header">
                                <i class="fas fa-table me-1"></i>
                                DataTable Akun
                            </div>
                            <div class="card-body">
                                <table id="datatablesSimple" class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>Nama</th>
                                            <th>Bio</th>
                                            <th>Email</th>
                                            <th>Level</th>
                                            <th>Dibuat Pada</th>
                                            <th>Jumlah Tugas</th>
                                            <th>Status</th>
                                            <th>Aksi</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        @foreach ($users as $user)
                                            <tr>
                                                <td>{{ $user->name }}</td>
                                                <td>{{ $user->profileBio ?? '-' }}</td>
                                                <td>{{ $user->email }}</td>
                                                <td>{{ $user->level }}</td>
                                                <td>{{ $user->created_at->format('d M Y') }}</td>
                                                <td>{{ $user->tugas_count }}</td>
                                                <td>
                                                    @if ($user->is_banned)
                                                    @php
                                                        $bannedUntil = Cache::get('banned_until_' . $user->id);
                                                    @endphp
                                                
                                                    <span class="badge bg-danger">Banned - Sisa waktu <span id="remaining-time">{{ $bannedUntil ? now()->diffForHumans($bannedUntil, ['parts' => 3]) : 'Tidak diketahui' }}</span></span>
                                                
                                                    <script>
                                                        // Jika ada waktu banned
                                                        @if ($bannedUntil)
                                                            var bannedUntil = @json($bannedUntil);
                                                            var remainingTimeElement = document.getElementById('remaining-time');
                                                
                                                            // Menghitung waktu yang tersisa secara real-time
                                                            function updateRemainingTime() {
                                                                var now = new Date();
                                                                var bannedUntilDate = new Date(bannedUntil);
                                                                var remainingTime = bannedUntilDate - now;
                                                
                                                                if (remainingTime <= 0) {
                                                                    remainingTimeElement.innerHTML = 'Waktu banned telah habis';
                                                                     // Berikan jeda 2 detik sebelum reload halaman
                                                                    setTimeout(function() {
                                                                        location.reload(); // Reload halaman setelah 2 detik
                                                                    }, 2000); 
                                                                } else {
                                                                    var hours = Math.floor(remainingTime / (1000 * 60 * 60));
                                                                    var minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
                                                                    var seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);
                                                
                                                                    remainingTimeElement.innerHTML = hours + ' jam ' + minutes + ' menit ' + seconds + ' detik';
                                                                }
                                                            }
                                                
                                                            // Update setiap detik
                                                            setInterval(updateRemainingTime, 1000);
                                                        @endif
                                                    </script>
                                                @else
                                                    <span class="badge bg-success">Aktif</span>
                                                @endif                                                

                                                </td>
                                                <td>
                                                    <!-- Tombol Ban/Unban -->
                                                    <form action="{{ route('admin.user.ban', $user->id) }}" method="POST" class="d-inline">
                                                        @csrf
                                                        <button type="submit" class="btn btn-warning btn-sm">
                                                            @if ($user->is_banned)
                                                                <i class="fas fa-check"></i> Unban
                                                            @else
                                                                <i class="fas fa-ban"></i> Ban
                                                            @endif
                                                        </button>
                                                    </form>
                                                
                                                    <!-- Tombol Hapus -->
                                                    <form action="{{ route('admin.user.hapus', $user->id) }}" method="POST" class="d-inline">
                                                        @csrf
                                                        @method('DELETE')
                                                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('Apakah Anda yakin ingin menghapus user ini?')">
                                                            <i class="fas fa-trash"></i> Hapus
                                                        </button>
                                                    </form>
                                                </td>
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
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/js/scripts.js')}}"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.js" crossorigin="anonymous"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/assets/demo/chart-area-demo.js')}}"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/assets/demo/chart-bar-demo.js')}}"></script>
        <script src="https://cdn.jsdelivr.net/npm/simple-datatables@7.1.2/dist/umd/simple-datatables.min.js" crossorigin="anonymous"></script>
        <script src="{{asset('startbootstrap-sb-admin-gh-pages/js/datatables-simple-demo.js')}}"></script>
    </body>
</html>
