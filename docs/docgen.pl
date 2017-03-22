#!/usr/bin/perl

use strict;
use warnings;

use JSON;

my $NO_HTTP_HEADER_PARAMS = <<'END_TEX';
HTTP Header Params & None. \\ \hline
END_TEX

my $NO_HTTP_BODY_PARAMS = <<'END_TEX';
HTTP Body Params & None. \\ \hline
END_TEX

sub _http_header_params($)
{
    my ( $http_header_params ) = @_;

    return $NO_HTTP_HEADER_PARAMS unless $http_header_params;

    my $retval = <<'END_TEX';
HTTP Header Params & \begin{tabular}{|l|l|}
END_TEX

    foreach my $param_name ( sort keys %$http_header_params )
    {
        my $description =  $http_header_params->{$param_name};
        $param_name     =~ s/_/\\_/g;

        $retval .= <<"END_TEX";
    \\texttt{$param_name} & $description \\\\ \\hline
END_TEX
    }

    $retval  =~ s/ \\hline$//;
    $retval .=  <<'END_TEX';
    \end{tabular} \\ \hline
END_TEX

    return $retval;
}

sub _http_body_params($)
{
    my ( $http_body_params ) = @_;

    return $NO_HTTP_BODY_PARAMS unless $http_body_params;

    my $retval = <<'END_TEX';
HTTP Body Params & \begin{tabular}{|l|l|}
END_TEX

    foreach my $param_name ( sort keys %$http_body_params )
    {
        my $description =  $http_body_params->{$param_name};
        $param_name     =~ s/_/\\_/g;

        $retval .= <<"END_TEX";
    \\texttt{$param_name} & $description \\\\ \\hline
END_TEX
    }

    $retval  =~ s/ \\hline$//;
    $retval .=  <<'END_TEX';
    \end{tabular} \\ \hline
END_TEX

    return $retval;
}

sub _output($)
{
    my ( $output ) = @_;

    my $retval = <<'END_TEX';
Output & \begin{tabular}{|l|l|}
END_TEX

    foreach my $param_name ( sort keys %$output )
    {
        my $description =  $output->{$param_name};
        $param_name     =~ s/_/\\_/g;

        $retval .= <<"END_TEX";
    \\texttt{$param_name} & $description \\\\ \\hline
END_TEX
    }

    $retval  =~ s/ \\hline$//;
    $retval .=  <<'END_TEX';
    \end{tabular} \\ \hline
END_TEX

    return $retval;
}

sub _route_hash_to_tex($$)
{
    my ( $route, $route_hash ) = @_;

    $route =~ s/{/\\{/g;
    $route =~ s/}/\\}/g;

    my $method      = $route_hash->{method};
    my $description = $route_hash->{description};

    my $retval = <<"END_TEX";
\\item \\textbf{$method $route} \\smallskip \\\\
\\begin{tabular}{|l|l|} \\hline
Description & $description \\\\ \\hline
END_TEX

    $retval .= _http_header_params( $route_hash->{http_header_params} );
    $retval .= _http_body_params( $route_hash->{http_body_params} );
    $retval .= _output( $route_hash->{output} );

    $retval .= <<'END_TEX';
\end{tabular}
END_TEX

    return $retval;
}

my $docspec_file = 'docspec.json';

open( my $docspec_fh, '<', $docspec_file ) or die "Could not open docspec: $!";

my $docspec = '';
$docspec   .= $_ while <$docspec_fh>;

close $docspec_fh;

my $docspec_struct = decode_json( $docspec );
my $latex_body     = <<'END_TEX';
\documentclass{article}

\usepackage{amsfonts}
\usepackage{amsmath}
\usepackage{amssymb}
\usepackage[margin=0.5in]{geometry}

\def\arraystretch{1.5}
\renewcommand{\labelitemi}{$\diamond$}

\begin{document}

Output fields marked with * can be passed into the HTTP request header to filter results.

\begin{itemize}
END_TEX

foreach my $route ( sort keys %$docspec_struct )
{
    $latex_body .= _route_hash_to_tex( $route, $docspec_struct->{$route} );
}

$latex_body .= <<'END_TEX';
\end{itemize}

\end{document}
END_TEX

open( my $api_latex_fh, '>', 'api.tex' ) or die "Could not open file handle for writing: $!";
print {$api_latex_fh} $latex_body;
close $api_latex_fh;

exit 0;
