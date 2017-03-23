#!/usr/bin/perl

use strict;
use warnings;

use feature 'switch';

use JSON;

my $NO_HTTP_HEADER_PARAMS = <<'END_TEX';
HTTP Header Params & None. \\ \hline
END_TEX

my $NO_HTTP_BODY_PARAMS = <<'END_TEX';
HTTP Body Params & None. \\ \hline
END_TEX

sub http_header_params($)
{
    my ( $http_header_params ) = @_;

    return $NO_HTTP_HEADER_PARAMS unless $http_header_params;

    my $retval = <<'END_TEX';
HTTP Header Params & \begin{tabular}{|p{4cm}|p{8cm}|}
END_TEX

    foreach my $param_name ( sort keys %$http_header_params )
    {
        my $description = $http_header_params->{$param_name}->{description};
        my $required    = $http_header_params->{$param_name}->{required};

        $param_name =~ s/_/\\_/g;
        $param_name =  "\\textcolor{red}{$param_name}" if defined $required && $required;

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

sub http_body_params($)
{
    my ( $http_body_params ) = @_;

    return $NO_HTTP_BODY_PARAMS unless $http_body_params;

    my $retval = <<'END_TEX';
HTTP Body Params & \begin{tabular}{|p{4cm}|p{8cm}|}
END_TEX

    foreach my $param_name ( sort keys %$http_body_params )
    {
        my $description = $http_body_params->{$param_name}->{description};
        my $required    = $http_body_params->{$param_name}->{required};

        $param_name =~ s/_/\\_/g;
        $param_name =  "\\textcolor{red}{$param_name}" if defined $required && $required;

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

sub output($)
{
    my ( $output ) = @_;

    my $retval = <<'END_TEX';
Output & \begin{tabular}{|p{4cm}|p{8cm}|}
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

sub route_hash_to_tex($$)
{
    my ( $full_route, $route_hash ) = @_;

    my ( $method, $route ) = split( /\s+/, $full_route, 2 );

    $route =~ s/{/\\{/g;
    $route =~ s/}/\\}/g;

    my $description = $route_hash->{description};

    my $retval = <<"END_TEX";
\\item \\textbf{$method $route} \\smallskip \\\\
\\begin{tabular}{|p{4cm}|p{12.85cm}|} \\hline
Description & $description \\\\ \\hline
END_TEX

    $retval .= http_header_params( $route_hash->{http_header_params} );
    $retval .= http_body_params( $route_hash->{http_body_params} );
    $retval .= output( $route_hash->{output} );

    $retval .= <<'END_TEX';
\end{tabular} \bigskip
END_TEX

    return $retval;
}

my $docspec_file = 'docspec.json';

open( my $docspec_fh, '<', $docspec_file ) or die "Could not open docspec: $!";

my $docspec = '';
$docspec   .= $_ while <$docspec_fh>;

close $docspec_fh;

my $docspec_struct = decode_json( $docspec );
my $gets           = {};
my $posts          = {};
my $puts           = {};
my $deletes        = {};

foreach my $full_route ( keys( %$docspec_struct ) )
{
    my ( $method, $route ) = split( /\s+/, $full_route, 2 );

    given( $method )
    {
        when( 'GET'    ) { $gets->{$full_route}    = $docspec_struct->{$full_route} }
        when( 'POST'   ) { $posts->{$full_route}   = $docspec_struct->{$full_route} }
        when( 'PUT'    ) { $puts->{$full_route}    = $docspec_struct->{$full_route} }
        when( 'DELETE' ) { $deletes->{$full_route} = $docspec_struct->{$full_route} }
    }
}

my $latex_body = <<'END_TEX';
\documentclass{article}

\usepackage{amsfonts}
\usepackage{amsmath}
\usepackage{amssymb}
\usepackage{color}
\usepackage[margin=0.5in]{geometry}

\def\arraystretch{1.5}
\renewcommand{\labelitemi}{$\diamond$}

\begin{document}

Notes:
\begin{enumerate}
    \item Output fields marked with * can be passed into the HTTP request header to filter results.
    \item Parameters in red are required.
    \item The following HTTP methods signify the following:
    \begin{enumerate}
        \item GET is used to retrieve data from the database.
        \item POST is used to insert new data into the database.
        \item PUT is used to update existing data in the database.
        \item DELETE is used to remove existing data from the database.
    \end{enumerate}
\end{enumerate}

\begin{itemize}
END_TEX

foreach my $method ( ( $gets, $posts, $puts, $deletes ) )
{
    foreach my $route( sort keys %$method )
    {
        $latex_body .= route_hash_to_tex( $route, $docspec_struct->{$route} );
    }
}

$latex_body .= <<'END_TEX';
\end{itemize}

\end{document}
END_TEX

open( my $api_latex_fh, '>', 'api.tex' ) or die "Could not open file handle for writing: $!";
print {$api_latex_fh} $latex_body;
close $api_latex_fh;

exit 0;
